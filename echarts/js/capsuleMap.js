/**
 * 地图
 */
var orderChart = echarts.init(document.getElementById('order'));

var areaData = [
    {name: "无锡市"},
    {name: "苏州市"},
    {name: "上海市"},
    {name: "成都市"},
    {name: "北京市"},
    {name: "杭州市"},
    {name: "南京市"},
    {name: "武汉市"}
];

var geoCoordMap = {
    '南京市': [118.80, 32.06],
    '杭州市': [120.16, 30.27],
    '成都市': [104.07, 30.57],
    '上海市': [121.48, 31.23],
    '北京市': [116.41, 39.91],
    '苏州市': [120.58, 31.30],
    '无锡市': [120.31, 31.49],
    '武汉市': [114.31, 30.59]
};

var convertData = function (data) {
    var res = [];
    for (var i = 0; i < data.length; i++) {
        var geoCoord = geoCoordMap[data[i].name];
        if (geoCoord) {
            res.push({
                name: data[i].name.substring(0,data[i].name.length-1),
                value: geoCoord.concat(data[i].countBooking)
            });
        }
    }
    return res;
};

option = {
    backgroundColor: '#0e012a',
    visualMap: {
        type: 'continuous',
        show: false,
        min: 0,
        max: 400,
        dimension:2,
        realtime: false,
        calculable: true,
        inRange: {
            color: ['lightskyblue','yellow', 'orangered']
        }
    },
    dataRange: {
        orient: 'horizontal',
        min: 0,
        max: 3000,
        text:['高','低'],           // 文本，默认为数值文本
        splitNumber:0,
        dimension:2
    },
    tooltip: {
        trigger: 'item',
        formatter: function (params) {
            var tooltpText ='';
            if(params.data.value[3]){
                var data = params.data.value[3];
                var userName =data.phone ? '用户'+(data.phone+'').substr((data.phone+'').length-4) : data.nick_name ? data.nick_name : '用户'+ data.uin;
                tooltpText = "<span>城市: " + data.city + "</span><br/>" +
                    "<span>用户名: " + userName + "</span><br/>" +
                    "<span>店铺: " + data.title+"</span><br/>" +
                    "<span>下单时间: " + data.booking_time + "</span>";
            }else{
                tooltpText = params.name
            }
            return tooltpText
        },
        enterable: true
    },
    geo: {
        map: 'china',
        zoom: 1.2,
        mapType: 'china',
        roam: false,
        left: 'center',
        //label: {
        //    normal: {
        //        show: true,
        //        textStyle:{color:"#c3c3c3"}
        //    },
        //    emphasis: {
        //        show: true,
        //        textStyle:{color:"#fff"}
        //    }
        //},
        itemStyle:{
            normal:{label:{show:true}},
            emphasis:{label:{show:true}}
        }
        //itemStyle:{
        //    normal:{
        //        label:{show:true},
        //        borderWidth:1,//省份的边框宽度
        //        borderColor:'#6ac9f5',//省份的边框颜色
        //        color:'#268de7',//地图背景颜色
        //        areaStyle:{color:'#f00'}//设置地图颜色
        //    },
        //    emphasis: {
        //        areaColor: '#2958dc'
        //    }
        //}
    },
    series: [
        {
            name: 'orderArea',
            type: 'effectScatter',
            coordinateSystem: 'geo',
            data: convertData(areaData),
            symbolSize: 8,
            showEffectOn: 'render',
            rippleEffect: {
                brushType: 'stroke'
            },
            selectedMode : 'multiple',
            hoverAnimation: true,
            itemStyle: {
                normal: {
                    color: '#f4e925',
                    shadowBlur: 10,
                    shadowColor: '#333',
                    normal:{label:{show:true}},
                    emphasis:{label:{show:true}}
                }
            },
            zlevel: 1
        }
    ]
};
orderChart.setOption(option);

