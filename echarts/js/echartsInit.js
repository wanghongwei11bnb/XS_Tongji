/**
 * 舱的使用率
 */
var servicePeopleType = 'day'; //服务人次 类型 日/周

// 基于准备好的dom，初始化echarts实例
var occupyChart = echarts.init(document.getElementById('occupy'));

// 基于准备好的dom，初始化echarts实例
var servicePeopleChart = echarts.init(document.getElementById('service_people'));

var timeChart = echarts.init(document.getElementById('time'));

$(document).ready(function(){

    document.getElementById('appraise_list').style.width = document.getElementsByClassName('appraise')[0].clientWidth-50+'px';

    $(".switch_wrap").click(function(e){    //这种点击方式怎么排除父元素？？？？
        var $clicked = $(e.target);    //e.target 捕捉到触发的元素
        var choice = $(e.target).attr('choice');
        servicePeopleType = choice;
        $clicked.addClass('active').siblings().removeClass('active');
        if(servicePeopleType === 'week'){
            var dataList = CumulativeBookingMessage.map(function (item) {
                return dateUtil('m-d',item[0]/1000);
            });
            var valueList = CumulativeBookingMessage.map(function (item) {
                return item[1];
            });
            servicePeopleChartDraw(dataList,valueList);
        }
        if(servicePeopleType === 'day'){
            var dataList = CumulativeBookingTodayMessage.map(function (item) {
                return dateUtil('h:i',item[0]/1000);
            });
            var valueList = CumulativeBookingTodayMessage.map(function (item) {
                return item[1];
            });
            servicePeopleChartDraw(dataList,valueList);
        }
    });
});

function occupyChartDraw(dateList,valueList,valueList2){
    var maxValue = Math.ceil(Math.max.apply(null, valueList));
    var maxValue2 = Math.ceil(Math.max.apply(null, valueList2));
    var max =  maxValue> maxValue2 ? maxValue : maxValue2;

    var minValue = Math.floor(Math.min.apply(null, valueList));
    var minValue2 = Math.floor(Math.min.apply(null, valueList2));
    var min = minValue < minValue2 ? minValue : minValue2;
    console.log(min,max)
    if(max-min < 5){
        max = min+5
    }

    occupyChart.setOption(option = {
        tooltip: {
            trigger: 'axis'
        },
        xAxis: [{
            data: dateList,
            axisLine: {
                lineStyle: {
                    color: '#ccc'
                }
            }
        }],
        yAxis: [{
            type: 'value',
            min: min,
            max: max,
            axisLabel: {
                formatter: '{value} %'
            },
            splitLine: {show: false},
            position: 'left',
            axisLine: {
                lineStyle: {
                    color: '#ccc'
                }
            }
        }],
        textStyle: {
            color: '#fff'
        },
        lable: {
            show: true
        },
        legend: {
            show: true,
            left: 'center',
            top:'12',
            data: [{name: '实时日使用率',
                icon: 'circle',
                textStyle: {
                    color: '#fff',
                    fontSize: 12
                }
            },
                {name: '累计日使用率',
                    icon: 'circle',
                    textStyle: {
                        color: '#fff',
                        fontSize: 12
                    }
                }
            ]
        },
        grid: {
            left: 50,
            right: 50,
            bottom:40
        },
        series: [{
            name: '实时日使用率',
            data: valueList,
            type: 'line',
            lineStyle: {
                color: '#731cc4'
            },
            itemStyle: {
                normal: {
                    color: '#731cc4'
                }
            }
        },{
            name: '累计日使用率',
            data: valueList2,
            type: 'line',
            lineStyle: {
                color: '#1bb3d3'
            },
            itemStyle: {
                normal: {
                    color: '#1bb3d3'
                }
            }
        }]
    });
}

function servicePeopleChartDraw(dateList,valueList){
    //var result = getMinMaxUtil(Math.min.apply(null, valueList),Math.max.apply(null, valueList));
    var min = Math.min.apply(null, valueList);
    var max = Math.max.apply(null, valueList);
    if(max-min<5){
        max=min+5
    }
    servicePeopleChart.setOption(option = {
        tooltip: {
            trigger: 'axis',
            formatter: function (params) {
                var tooltpText = "<span>时间："+params[0].name+"</span><br/>" +
                    "<span>服务人次: "+params[0].value+"人</span>";
                return tooltpText
            }
        },
        xAxis: [{
            data: dateList,
            axisLine: {
                lineStyle: {
                    color: '#ccc'
                }
            }
        }],
        yAxis: [{
            min: min,
            max: max,
            axisLabel: {
                formatter: '{value}'
            },
            splitLine: {show: false},
            axisLine: {
                lineStyle: {
                    color: '#ccc'
                }
            }
        }],
        textStyle: {
            color: '#fff'
        },
        lable: {
            show: true
        },
        grid: {
            left: 65,
            right: 30,
            bottom:30
        },
        series: [{
            data: valueList,
            type: 'line',
            lineStyle: {
                color: '#474fcc'
            },
            itemStyle: {
                normal: {
                    color: '#474fcc'
                }
            }
            //smooth: true,
            //areaStyle: {
            //    color: '#189df9'
            //}
        }]
    });
}

function timeChartDraw(dateList,valueList){
    var result = getMinMaxUtil(Math.min.apply(null, valueList),Math.max.apply(null, valueList));
    var min = result.min;
    var max = result.max;

    timeChart.setOption(option = {
        tooltip: {
            trigger: 'axis',
            formatter: function (params) {
                var tooltpText = "<span>时间："+params[0].name+"</span><br/>" +
                    "<span>使用时长: "+params[0].value+"小时</span><br/>";
                return tooltpText
            }
        },
        xAxis: [{
            data: dateList,
            axisLine: {
                lineStyle: {
                    color: '#ccc'
                }
            }
        }],
        yAxis: [{
            min: min,
            max: max,
            axisLabel: {
                formatter: '{value}'
            },
            splitLine: {show: false},
            axisLine: {
                lineStyle: {
                    color: '#ccc'
                }
            }
        }],
        textStyle: {
            color: '#fff'
        },
        lable: {
            show: true
        },
        grid: {
            left: 65,
            right: 30,
            bottom: 40
        },
        series: [{
            data: valueList,
            type: 'line',
            lineStyle: {
                color: '#57b3fb'
            },
            itemStyle: {
                normal: {
                    color: '#57b3fb'
                }
            }
        }]
    });
}


