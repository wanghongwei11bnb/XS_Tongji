/**
 * 地图
 */
var orderChart = echarts.init(document.getElementById('order'));

var areaData = [
    {name: "无锡"},
    {name: "保定"},
    {name: "苏州"},
    {name: "重庆"},
    {name: "深圳"},
    {name: "上海"},
    {name: "成都"},
    {name: "北京"},
    {name: "杭州"},
    {name: "南京"},
    {name: "武汉"}
];

var mapData = [];

var geoCoordMap = {};

orderChart.setOption({
    series: [{
        type: 'map',
        map: 'china',
        zoom: 1.2
    }]
});

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
    backgroundColor: '#0e012a',
    tooltip: {
        trigger: 'item',
        formatter: function (params) {
            var tooltpText ='';
            if(params.data && params.data.value[2]){
                var data = params.data.value[2];
                tooltpText = "<div id='tipAnimate'>"+
                    "<span>城市: " + data.city + "</span><br/>" +
                    "<span>用户名: " + data.user_name + "</span><br/>" +
                    "<span>店铺: " + data.title+"</span><br/>" +
                    "<span>下单时间: " + data.booking_time + "</span>"+
                "</div>";
            }else{
                tooltpText = params.name
            }
            return tooltpText
        },
        enterable: true
    },
    visualMap: {
        min: 0,
        max: 1,
        show: false,
        left: 'left',
        top: 'bottom',
        text: ['高', '低'],
        calculable: true,
        inRange: {
            color: ['#52b2fd', '#198aea']
        }
    },
    geo: {
        map: 'china',
        zoom: 1.2,
        label: {
            normal: {
                show: true,
                color: '#fff'
            },
            emphasis: {
                show: false,
                color: '#fff'
            }
        },
        roam: false,
        itemStyle: {
            normal: {
                areaColor: '#52b2fd',
                borderColor: '#ddd'
            },
            emphasis: {
                areaColor: '#ffca00'
            }
        }
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
            //selectedMode : 'multiple',
            hoverAnimation: true,
            label: {
                normal: {
                    formatter: '{b}',
                    position: 'right',
                    show: false
                },
                emphasis: {
                    show: true
                }
            },
            itemStyle: {
                color: '#c60fff',
                shadowBlur: 10,
                shadowColor: '#333'
            },
            emphasis: {
                itemStyle: {
                    color: '#c60fff',
                    shadowBlur: 10,
                    shadowColor: '#333'
                }
            }
        },
        {
            name: 'orderMap',
            type: 'map',
            mapType: 'china',
            geoIndex: 0,
            label: {
                normal: {
                    show: true
                },
                emphasis: {
                    show: true
                }
            },
            data: mapData
        }
    ]
};
orderChart.setOption(option);

