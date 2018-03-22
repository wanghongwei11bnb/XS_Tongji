/**
 * websocket
 */
var appraiseTimer;
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
    wsUrl = 'ws://dev.tj.xiangshuispace.com/tj';
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
        var data = JSON.parse(event.data);
        console.log(data);
        //收到 初始化 数据
        if(data.messageType === 'ListMessage'){
            var messageList = data.messageList;
            for(var key in messageList){
                var listData = messageList[key];
                if(listData.messageType === 'ContractMessage'){
                    if(listData.cityList && listData.cityList.length > 0){
                        cityList = listData.cityList;
                        var newAreaData = [];
                        for(var key in cityList){
                            newAreaData.push({
                                name: cityList[key].city
                            })
                        }
                        areaData = newAreaData;
                        orderChart.setOption({
                            series: [{
                                // 根据名字对应到相应的系列
                                name: 'orderArea',
                                data: convertData(areaData)
                            }]
                        });
                    }

                }
                if(listData.messageType === 'InitAppraiseMessage'){
                    if(listData.appraiseList && listData.appraiseList.length > 0){
                        appraiseArr = listData.appraiseList;
                        appraiseTimer=setInterval(function(){
                            var sTxt=appraiseArr.shift();
                            createDom(sTxt,'appraise_list');
                            appraiseArr.push(sTxt);
                        },2000);
                    }
                }
                if(listData.messageType === 'UsageRateMessage'){
                    if(listData.data && listData.data.length > 0){
                        var dateList = listData.data.map(function (item) {
                            return dateUtil('m-d h:i',item[0]/1000);
                        });
                        var valueList = listData.data.map(function (item) {
                            return (item[1] * 100).toFixed(2);
                        });
                        occupyChartDraw(dateList,valueList);
                    }
                }
                if(listData.messageType === 'CumulativeBookingMessage'){
                    if(listData.data && listData.data.length > 0){
                        var listServiceDateList = listData.data.map(function (item) {
                            return dateUtil('m-d',item[0]/1000);
                        });
                        var listServiceValueList = listData.data.map(function (item) {
                            return item[1];
                        });
                        servicePeopleChartDraw(listServiceDateList,listServiceValueList)
                    }
                }
                if(listData.messageType === 'CumulativeTimeMessage'){
                    if(listData.data && listData.data.length > 0){
                        var listTimeDateList = listData.data.map(function (item) {
                            return dateUtil('m-d',item[0]/1000);
                        });
                        var listTimeValueList = listData.data.map(function (item) {
                            return (item[1]/1000/60/60).toFixed(2);
                        });
                        timeChartDraw(listTimeDateList,listTimeValueList)
                    }
                }
            }
        }

        //收到的推送消息是 评论
        if(data.messageType === 'PushAppraiseMessage'){
            if(appraiseTimer){
                clearInterval(appraiseTimer)
            }
            receiveAppraiseflag = true;
            var acceptData = data.appraise;
            appraiseArr.unshift(acceptData);
            createDom(acceptData,'appraise_list');
            if(appraiseArr.length > 5){
                appraiseArr.pop()
            }
        }
        //收到的推送消息是 订单
        if(data.messageType === "PushBookingMessage"){
            var orderIndex;
            for(var k in areaData){
                if(areaData[k].name === data.area.city){
                    orderIndex = k
                }
            }
            var province = cityList[orderIndex].province;
            province = province.substring(0,province.length-1);
            areaData[orderIndex].value={
                'city' : data.area.city,
                'user_name': data.booking.uin,
                'title': data.area.title,
                'booking_time': dateUtil('Y-m-d h:i:s',data.booking.create_time)
            };
            orderChart.dispatchAction({
                type: 'mapToggleSelect',
                // 可选，系列 index，可以是一个数组指定多个系列
                seriesIndex: 0,
                // 数据的 index，如果不指定也可以通过 name 属性根据名称指定数据
                name: data.area.city
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
        }
        //收到的推送消息是 舱的使用率
        if(data.messageType === "UsageRateMessage"){
            var dateList = data.data.map(function (item) {
                return dateUtil('m-d h:i',item[0]/1000);
            });
            var valueList = data.data.map(function (item) {
                return (item[1] * 100).toFixed(2);
            });

            occupyChart.setOption(option = {
                xAxis: [{
                    data: dateList
                }],
                series: [{
                    data: valueList,
                    type: 'line'
                }]
            });
        }
        //收到的推送消息是 服务人次
        if(data.messageType === "CumulativeBookingMessage"){
            var serviceDateList = data.data.map(function (item) {
                return dateUtil('m-d',item[0]/1000);
            });
            var serviceValueList = data.data.map(function (item) {
                return item[1];
            });
            servicePeopleChart.setOption(option = {
                visualMap: [{
                    max: Math.max.apply(null, serviceValueList)
                }],
                xAxis: [{
                    data: serviceDateList
                }],
                series: [{
                    data: serviceValueList
                }]
            });
        }
        //收到的推送消息是 使用时长
        if(data.messageType === 'CumulativeTimeMessage'){
            var timeDateList = data.data.map(function (item) {
                return dateUtil('m-d',item[0]/1000);
            });
            var timeValueList = data.data.map(function (item) {
                return (item[1]/1000/60/60).toFixed(2);
            });

            timeChart.setOption(option = {
                visualMap: [{
                    max: Math.max.apply(null, timeValueList)
                }],
                xAxis: [{
                    data: timeDateList
                }],
                series: [{
                    data: timeValueList
                }]
            });
        }

        setTimeout(function () {
            if(receiveAppraiseflag){
                if(appraiseTimer){
                    clearInterval(appraiseTimer)
                }
                appraiseTimer=setInterval(function(){
                    var sTxt=appraiseArr.shift();
                    console.log(sTxt)
                    createDom(sTxt,'appraise_list');
                    appraiseArr.push(sTxt);
                },2000);
                receiveAppraiseflag = false
            }
        },300);
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

