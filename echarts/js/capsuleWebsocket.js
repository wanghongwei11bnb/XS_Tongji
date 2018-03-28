/**
 * websocket
 */
var appraiseTimer;
var timeOutTimer;
var appraiseArr = [];
var cityList = [];
var receiveAppraiseflag = false;

var socket;//websocket实例
var lockReconnect = false;//避免重复连接

var wsUrl;
var hostname = window.location.hostname;
if(hostname === 'dev.tj.xiangshuispace.com'){
    wsUrl = 'ws://dev.tj.xiangshuispace.com/tj';
}else if(hostname === 'tj.xiangshuispace.com'){
    wsUrl = 'ws://tj.xiangshuispace.com/tj';
}else{
    //ws://192.168.1.99:8080/tj
    wsUrl = 'ws://tj.xiangshuispace.com/tj';
}

//处理部分相似的message
function handleApartMessage(data,type){
    var dateList;
    var valueList;
    var valueList2;
    if(type === 'UsageRateMessage'){
        dateList = data.map(function (item) {
            return dateUtil('m-d h:i',item[0]/1000);
        });
        valueList = data.map(function (item) {
            return (item[1] * 100).toFixed(2);
        });
        valueList2 = data.map(function (item) {
            return (item[2] * 100).toFixed(2);
        });
        occupyChartDraw(dateList,valueList,valueList2);
    }else if(type === 'CumulativeBookingMessage'){
        dateList = data.map(function (item) {
            return dateUtil('m-d',item[0]/1000);
        });
        valueList = data.map(function (item) {
            return item[1];
        });
        servicePeopleChartDraw(dateList,valueList);
    }else if(type === 'CumulativeTimeMessage'){
        dateList = data.map(function (item) {
            return dateUtil('m-d',item[0]/1000);
        });
        valueList = data.map(function (item) {
            return (item[1]/1000/60/60).toFixed(2);
        });
        timeChartDraw(dateList,valueList);
    }
}

//数据重置
function dataReset(){
    if(appraiseTimer){
        clearInterval(appraiseTimer)
    }
    appraiseTimer='';
    appraiseArr = [];
    cityList = [];
    receiveAppraiseflag = false;
}

function createWebSocket(url) {
    try {
        socket = new WebSocket(url);
        initEventHandle();
    } catch (e) {
        reconnect(url);
    }
}

//处理推送信息
function doMessage(message){
    switch (message.messageType){
        case "ListMessage":
            for(var key in message.messageList){
                doMessage(message.messageList[key]);
            }
            break;
        case "InitAppraiseMessage":
            if(message.appraiseList && message.appraiseList.length > 0){
                appraiseArr = message.appraiseList;
                appraiseTimer=setInterval(function(){
                    var sTxt=appraiseArr.shift();
                    createDom(sTxt,'appraise_list');
                    appraiseArr.push(sTxt);
                },2000);
            }
            break;
        case "ContractMessage":
            if(message.cityList && message.cityList.length > 0){
                cityList = message.cityList;
                var newAreaData = [];
                for(var key in cityList){
                    newAreaData.push({
                        name: cityList[key].city,
                        countBooking: cityList[key].countBooking
                    })
                }
                areaData = newAreaData;
                console.log(convertData(areaData))
                orderChart.setOption({
                    series: [{
                        // 根据名字对应到相应的系列
                        name: 'orderArea',
                        data: convertData(areaData)
                    }]
                });
            }
            break;
        case "PushBookingMessage":
            var orderIndex;
            for(var k in areaData){
                if(areaData[k].name === message.area.city){
                    orderIndex = k
                }
            }
            var province = cityList[orderIndex].province;
            province = province.substring(0,province.length-1);
            areaData[orderIndex].value={
                'city' : message.area.city,
                'user_name': message.booking.nick_name,
                'title': message.area.title,
                'booking_time': dateUtil('Y-m-d h:i:s',message.booking.create_time)
            };
            orderChart.dispatchAction({
                type: 'mapToggleSelect',
                // 可选，系列 index，可以是一个数组指定多个系列
                seriesIndex: 0,
                // 数据的 index，如果不指定也可以通过 name 属性根据名称指定数据
                name: message.area.city
            });
            orderChart.setOption({
                series: [{
                    // 根据名字对应到相应的系列
                    name: 'orderArea',
                    data: convertData(areaData)
                }],
                geo: {
                    regions: [{
                        name: province,
                        selected: true
                    }]
                }
            });
            orderChart.dispatchAction({
                type: 'showTip',
                seriesIndex: 0,
                dataIndex: orderIndex
            });
            break;
        case "PushAppraiseMessage":
            // 收到消息 清除手动播放循环
            if(appraiseTimer){
                clearInterval(appraiseTimer)
            }
            // 收到消息 清除准备启动手动循环的timeout
            if (timeOutTimer) {
                clearTimeout(timeOutTimer)
            }
            var acceptData = message.appraise;
            appraiseArr.unshift(acceptData);
            createDom(acceptData,'appraise_list');
            if(appraiseArr.length > 5){
                appraiseArr.pop()
            }
            appraiseArr.push(appraiseArr.shift());
            //2s 内没有收到消息就会执行下面的代码
            timeOutTimer = setTimeout(function () {
                appraiseTimer=setInterval(function(){
                    var sTxt = appraiseArr.shift();
                    createDom(sTxt,'appraise_list');
                    appraiseArr.push(sTxt);
                },2000);
            }, 2000);
            break;
        case "UsageRateMessage":
            if(message.data && message.data.length > 0){
                handleApartMessage(message.data,"UsageRateMessage");
            }
            break;
        case "CumulativeBookingMessage":
            if(message.data && message.data.length > 0){
                handleApartMessage(message.data,"CumulativeBookingMessage");
            }
            break;
        case "CumulativeTimeMessage":
            if(message.data && message.data.length > 0){
                handleApartMessage(message.data,"CumulativeTimeMessage");
            }
            break;
        default :break;
    }
}

function initEventHandle() {
    socket.onclose = function () {
        dataReset();
        reconnect(wsUrl);
    };
    socket.onerror = function () {
        dataReset();
        reconnect(wsUrl);
    };
    socket.onopen = function () {
        console.log("Client notified socket has opened");
        // 发送一个初始化消息
        socket.send(JSON.stringify({msg_type: 'test',name: String(100000)}));
        socketTimer = setInterval(function () {
            socket.send(JSON.stringify({ msg_type: "ping" }))
        },50000);
        //心跳检测重置
        heartCheck.reset().start();
    };
    socket.onmessage = function(event) {
        //如果获取到消息，心跳检测重置
        //拿到任何消息都说明当前连接是正常的
        heartCheck.reset().start();
        console.log('Client received a message');
        var data = eval("("+event.data+")");
        console.log(data);
        doMessage(data);
    };
}

function reconnect(url) {
    dataReset();
    if(lockReconnect) return;
    lockReconnect = true;
    //没连接上会一直重连，设置延迟避免请求过多
    setTimeout(function () {
        createWebSocket(url);
        lockReconnect = false;
    }, 2000);
}


//心跳检测
var heartCheck = {
    timeout: 60000,//60秒
    timeoutObj: null,
    serverTimeoutObj: null,
    reset: function(){
        clearTimeout(this.timeoutObj);
        clearTimeout(this.serverTimeoutObj);
        return this;
    },
    start: function(){
        var self = this;
        this.timeoutObj = setTimeout(function(){
            //这里发送一个心跳，后端收到后，返回一个心跳消息，
            //onmessage拿到返回的心跳就说明连接正常
            socket.send("HeartBeat");
            self.serverTimeoutObj = setTimeout(function(){//如果超过一定时间还没重置，说明后端主动断开了
                socket.close();//如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
            }, self.timeout)
        }, this.timeout)
    }
};

createWebSocket(wsUrl);

