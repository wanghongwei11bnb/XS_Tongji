/**
 * 舱的使用率
 */
// 基于准备好的dom，初始化echarts实例
var occupyChart = echarts.init(document.getElementById('occupy'));

// 基于准备好的dom，初始化echarts实例
var servicePeopleChart = echarts.init(document.getElementById('service_people'));

var timeChart = echarts.init(document.getElementById('time'));

function occupyChartDraw(dateList,valueList,valueList2){
    var maxValue = Math.ceil(Math.max.apply(null, valueList));
    var maxValue2 = Math.ceil(Math.max.apply(null, valueList2));
    var max =  maxValue> maxValue2 ? maxValue : maxValue2;

    var minValue = Math.floor(Math.min.apply(null, valueList));
    var minValue2 = Math.floor(Math.min.apply(null, valueList2));
    var min = minValue < minValue2 ? minValue : minValue2;

    occupyChart.setOption(option = {
        // title: {
        //     text: '使用率',
        //     top: '5%',
        //     left: -4,
        //     textStyle: {
        //         color: '#fff',
        //         fontWeight: 'bold',
        //         fontSize: 14
        //     }
        // },
        //visualMap: [{
        //    show: false,
        //    type: 'continuous',
        //    seriesIndex: 0,
        //    min: min,
        //    max: max
        //}],
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
        },{
            type: 'value',
            min: min,
            max: max,
            axisLabel: {
                formatter: '{value} %'
            },
            splitLine: {show: false},
            position: 'right',
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
            left: 10,
            top:'5%',
            data: [{name: '使用率',
                    icon: 'circle',
                    textStyle: {
                        color: '#fff',
                        fontSize: 14,
                    }
                   },
                    {name: '累计使用率',
                    icon: 'circle',
                    textStyle: {
                        color: '#fff',
                        fontSize: 14,
                    }
                }
            ]
        },
        grid: {
            left: 50,
            right: 100
        },
        series: [{
            name: '使用率',
            data: valueList,
            type: 'line',
            lineStyle: {
                color: '#8ecefc'
            },
            itemStyle: {
                normal: {
                    color: '#8ecefc'
                }
            }
            //smooth: true,
            //areaStyle: {
            //    color: '#189df9'
            //}
        },{
            name: '累计使用率',
            data: valueList2,
            type: 'line',
            lineStyle: {
                color: '#7cedc4'
            },
            itemStyle: {
                normal: {
                    color: '#7cedc4'
                }
            }
        }]
    });
}

function servicePeopleChartDraw(dateList,valueList){
    // var min = parseInt(Math.min.apply(null, valueList))-100;
    // var max = parseInt(Math.max.apply(null, valueList))+100;
    var result = getMinMaxUtil(Math.min.apply(null, valueList),Math.max.apply(null, valueList));
    var min = result.min;
    var max = result.max;
    servicePeopleChart.setOption(option = {
        title: {
            text: '服务人次',
            top: '5%',
            left: 0,
            textStyle: {
                color: '#fff',
                fontWeight: 'bold',
                fontSize: 14
            }
        },
        visualMap: [{
            show: false,
            type: 'continuous',
            seriesIndex: 0,
            min: min,
            max: max
        }],
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
            //name: '单位: %',
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
            left: 60,
            right: 30
        },
        series: [{
            data: valueList,
            type: 'line'
            //smooth: true,
            //areaStyle: {
            //    color: '#189df9'
            //}
        }]
    });
}

function timeChartDraw(dateList,valueList){
    // var min = parseInt(Math.min.apply(null, valueList))-100;
    // var max = parseInt(Math.max.apply(null, valueList))+100;
    var result = getMinMaxUtil(Math.min.apply(null, valueList),Math.max.apply(null, valueList));
    var min = result.min;
    var max = result.max;

    timeChart.setOption(option = {
        title: {
            text: '使用时长',
            top: '5%',
            left: 0,
            textStyle: {
                color: '#fff',
                fontWeight: 'bold',
                fontSize: 14
            }
        },
        visualMap: [{
            show: false,
            type: 'continuous',
            seriesIndex: 0,
            min: min,
            max: max
        }],
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
            //name: '单位: %',
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
            left: 60,
            right: 30
        },
        series: [{
            data: valueList,
            type: 'line'
            //smooth: true,
            //areaStyle: {
            //    color: '#189df9'
            //}
        }]
    });
}