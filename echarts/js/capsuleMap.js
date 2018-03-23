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
                name: data[i].name,
                value: geoCoord.concat(data[i].value)
            });
        }
    }
    return res;
};

option = {
    backgroundColor: '#404a59',
    title: {
        text: '享+共享头等舱实时监控平台',
        x: 'center',
        y: '2%',
        textStyle: {
            color: '#fff',
            fontWeight: 'bold',
            fontSize: 18
        }
    },
    tooltip: {
        trigger: 'item',
        formatter: function (params) {
            var tooltpText ='';
            if(params.data.value[2]){
                tooltpText = "<span>城市: "+params.data.value[2].city+"</span><br/>" +
                    "<span>用户名: "+params.data.value[2].user_name+"</span><br/>" +
                    "<span>店铺: "+params.data.value[2].title+"</span><br/>" +
                    "<span>下单时间: "+params.data.value[2].booking_time+"</span>";
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
        label: {
            normal: {
                show: true,
                textStyle:{color:"#c3c3c3"}
            },
            emphasis: {
                show: true,
                textStyle:{color:"#fff"}
            }
        },
        itemStyle: {
            normal: {
                areaColor: '#323c48',
                borderColor: '#111'
            },
            emphasis: {
                areaColor: '#189df9'
            }
        }
        //itemStyle:{
        //    normal:{
        //        label:{show:true},
        //        borderWidth:1,//省份的边框宽度
        //        borderColor:'#f60',//省份的边框颜色
        //        color:'#ece2df',//地图背景颜色
        //        areaStyle:{color:'#f60'}//设置地图颜色
        //    },
        //    emphasis:{label:{show:true}}
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
//                label: {
//                    normal: {
//                        formatter: '{b}',
//                        position: 'right',
//                        show: true
//                    }
//                },
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

