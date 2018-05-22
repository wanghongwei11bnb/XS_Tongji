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
    wsUrl = 'ws://192.168.1.42:8080/tj';
}

var orderList = [];
var CumulativeBookingTodayMessage = [];
var CumulativeBookingMessage = [];

//处理部分相似的message
function handleApartMessage(data,type){
    var dataList;
    var valueList;
    var valueList2;
    if(type === 'UsageRateMessage'){
        dataList = data.map(function (item) {
            return dateUtil('m-d h:i',item[0]/1000);
        });
        valueList = data.map(function (item) {
            return (item[1] * 100).toFixed(2);
        });
        valueList2 = data.map(function (item) {
            return (item[2] * 100).toFixed(2);
        });
        occupyChartDraw(dataList,valueList,valueList2);
    }else if(type === 'CumulativeBookingMessage'){
        CumulativeBookingMessage = data;
        if(servicePeopleType === 'week'){
            dataList = data.map(function (item) {
                return dateUtil('m-d',item[0]/1000);
            });
            valueList = data.map(function (item) {
                return item[1];
            });
            servicePeopleChartDraw(dataList,valueList);
        }
    }else if(type === 'CumulativeTimeMessage'){
        dataList = data.map(function (item) {
            return dateUtil('m-d',item[0]/1000);
        });
        valueList = data.map(function (item) {
            return (item[1]/1000/60/60).toFixed(2);
        });
        timeChartDraw(dataList,valueList);
    }else if(type === 'CumulativeBookingTodayMessage'){
        CumulativeBookingTodayMessage = data;
        if(servicePeopleType === 'day'){
            dataList = data.map(function (item) {
                return dateUtil('h:i',item[0]/1000);
            });
            valueList = data.map(function (item) {
                return item[1];
            });
            servicePeopleChartDraw(dataList,valueList);
        }
    }
}

//数据重置
function dataReset(){
    if(appraiseTimer){
        clearInterval(appraiseTimer)
    }
    appraiseTimer='';
    appraiseArr = [];
    receiveAppraiseflag = false;
}

//初始化评论
function handleInitAppraiseMessage(message){
    if(message.appraiseList && message.appraiseList.length > 0){
        appraiseArr = message.appraiseList;
        appraiseTimer=setInterval(function(){
            var sTxt=appraiseArr.shift();
            createDom(sTxt,'appraise_list');
            appraiseArr.push(sTxt);
        },2000);
    }
}

//初始化城市
function handleContractMessage(message){
    if(message.cityList && message.cityList.length > 0){
        cityList = message.cityList;
        var newAreaData = [];
        var mapData = [];
        var newGeoCoordMap = [];
        for(var key in cityList){
            cityList[key].city = cityList[key].city.substring(0,cityList[key].city.length-1);
            newAreaData.push({
                name: cityList[key].city
            });
            var provinceOrigin  = cityList[key].province;
            mapData.push({
                name: provinceOrigin.substring(0,provinceOrigin.length-1),
                value: 1
            });
            newGeoCoordMap[cityList[key].city] = [cityList[key].lng,cityList[key].lat]
        }
        areaData = newAreaData;
        geoCoordMap = newGeoCoordMap;

        orderChart.setOption({
            series: [{
                // 根据名字对应到相应的系列
                name: 'orderArea',
                data: convertData(areaData)
            },{
                name: 'orderMap',
                data: mapData
            }]
        });
    }
}

//展示订单推送
function innerOrder(id,data){
    var orderStatus  = data.status
    $('#'+id).parent('.order_item').css({opacity: 0});
    if(orderList.length > 2){
        if(id === 'order_item_three'){
            $('.order_one').css({opacity: 0});
        }
        if(id === 'order_item_one'){
            $('.order_two').css({opacity: 0});
        }
        if(id === 'order_item_two'){
            $('.order_three').css({opacity: 0});
        }
    }
    if($('#'+id).parent('.order_item').hasClass('order_item_end')){
        $('#'+id).parent('.order_item').removeClass('order_item_end');
    }
    if(orderStatus == 4){
        $('#'+id).parent('.order_item').addClass('order_item_end');
    }
    setTimeout(function () {
        $('#'+id).html("<span>城市: " + data.city + "</span><br/>" +
            "<span>用户名: " + data.user_name + "</span><br/>" +
            "<span>店铺: " + data.title+"</span><br/>" +
            "<span>" + (orderStatus != 4 ? "下单时间: " : "结束时间: ")+ (orderStatus != 4 ? data.booking_time : data.end_time) + "</span>");
        $('#'+id).parent('.order_item').css({opacity: 1});
        if($('#'+id).hasClass('tipAnimate')){
            $('#'+id).removeClass('tipAnimate');
            setTimeout(function () {
                $('#'+id).addClass('tipAnimate');
            },1000/60);
        }else{
            $('#'+id).addClass('tipAnimate');
        }
    },200);
}

//处理 订单推送
function handlePushBookingMessage(message){
    var orderIndex;
    for(var k in areaData){
        if(areaData[k].name === message.area.city.substring(0, message.area.city.length - 1)){
            orderIndex = k
        }
    }
    var province = cityList[orderIndex].province;
    province = province.substring(0,province.length-1);

    var lastedOrder = {
        'city' : message.area.city,
        'user_name': '用户'+ (message.booking.phone ? subLastStringUtil(message.booking.phone,4) : subLastStringUtil(message.booking.uin,4)),
        'title': message.area.title,
        'booking_time': dateUtil('Y-m-d h:i',message.booking.create_time),
        'create_time': message.booking.create_time,
        'status': message.booking.status,
        'end_time': dateUtil('Y-m-d h:i',message.booking.end_time),
        'time_stamp': new Date().getTime()
    };
    if(orderList.length < 3){
        orderList.push(lastedOrder);
        if(orderList.length == 1){
            innerOrder('order_item_one',orderList[0])
        }else if(orderList.length == 2){
            innerOrder('order_item_two',orderList[1])
        }else if(orderList.length == 3){
            innerOrder('order_item_three',orderList[2])
        }
        //innerOrder('order_item_' + orderList.length == 1 ? 'one' : orderList.length == 2 ? 'two' : 'three',orderList[orderList.length-1])
    }else{
        var minTimeStamp = orderList[0].time_stamp;
        var minKey= 0;
        for(var key in orderList){
            if(orderList[key].time_stamp < minTimeStamp){
                minTimeStamp = orderList[key].time_stamp;
                minKey = key;
            }
        }
        orderList[minKey] = lastedOrder;
        innerOrder(minKey==0 ? 'order_item_one' : minKey==1 ? 'order_item_two' : 'order_item_three',orderList[minKey])
    }
    //areaData[orderIndex].value={
    //    'city' : message.area.city,
    //    'user_name': '用户'+ (message.booking.phone ? subLastStringUtil(message.booking.phone,4) : subLastStringUtil(message.booking.uin,4)),
    //    'title': message.area.title,
    //    'booking_time': dateUtil('Y-m-d h:i',message.booking.create_time)
    //};
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
    //orderChart.dispatchAction({
    //    type: 'showTip',
    //    seriesIndex: 0,
    //    dataIndex: orderIndex
    //});
    //var audio = document.getElementById('music');
    //if(audio!==null && audio.readyState === 4){
    //    audio.play();//audio.play();// 这个就是播放
    //}
}

//处理 评论推送
function handlePushAppraiseMessage(message){
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
            handleInitAppraiseMessage(message);
            break;
        case "ContractMessage":
            handleContractMessage(message);
            break;
        case "PushBookingMessage":
            handlePushBookingMessage(message);
            break;
        case "PushAppraiseMessage":
            handlePushAppraiseMessage(message);
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
        case "CumulativeBookingTodayMessage":
            if(message.data && message.data.length > 0){
                handleApartMessage(message.data,"CumulativeBookingTodayMessage");
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
        //console.log("Client notified socket has opened");
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
        //console.log('Client received a message');
        var data = eval("("+event.data+")");
        // console.log(data);
        doMessage(data);
    };
}

function reconnect(url) {
    dataReset();
    if(lockReconnect) return;
    lockReconnect = true;
    //没连接上会一直重连，设置延迟避免请求过多
    setTimeout(function () {
        if(areaData && areaData.length > 0 && cityList && cityList.length > 0){
            url+= '?reconnect=1';
        }
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

$('')

