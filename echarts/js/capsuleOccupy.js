/**
 * 舱的使用率
 */
// 基于准备好的dom，初始化echarts实例
var occupyChart = echarts.init(document.getElementById('occupy'));

// 基于准备好的dom，初始化echarts实例
var servicePeopleChart = echarts.init(document.getElementById('service_people'));

var timeChart = echarts.init(document.getElementById('time'));

function occupyChartDraw(dateList,valueList){
    occupyChart.setOption(option = {
        title: {
            text: '头等舱使用率',
            top: '5%',
            left: -4,
            textStyle: {
                color: '#fff',
                fontWeight: 1200,
                fontSize: 14
            }
        },
        visualMap: [{
            show: false,
            type: 'continuous',
            seriesIndex: 0,
            min: 0,
            max: 100
        }],
        tooltip: {
            trigger: 'axis',
            formatter: function (params) {
                var tooltpText = "<span>使用率: "+params[0].value+"%</span><br/>" +
                    "<span>"+params[0].name+"</span><br/>";
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
            min: 0,
            max: 100,
            axisLabel: {
                formatter: '{value} %'
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
            left: 40,
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

function servicePeopleChartDraw(dateList,valueList){
    servicePeopleChart.setOption(option = {
        title: {
            text: '头等舱服务人次（单位:人）',
            top: '5%',
            left: 15,
            textStyle: {
                color: '#fff',
                fontWeight: 1200,
                fontSize: 14
            }
        },
        visualMap: [{
            show: false,
            type: 'continuous',
            seriesIndex: 0,
            min:0,
            max: Math.max.apply(null, valueList)
        }],
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
            //name: '单位: %',
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
            left: 40,
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
    timeChart.setOption(option = {
        title: {
            text: '头等舱使用时长（单位:小时）',
            top: '5%',
            left: 0,
            textStyle: {
                color: '#fff',
                fontWeight: 1200,
                fontSize: 14
            }
        },
        visualMap: [{
            show: false,
            type: 'continuous',
            seriesIndex: 0,
            min:0,
            max: Math.max.apply(null, valueList)
        }],
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
            //name: '单位: %',
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
            left: 40,
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