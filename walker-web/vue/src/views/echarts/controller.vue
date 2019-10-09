<template>
  <div class="app-container" >


    <div>mvc统计</div>

<!--搜索-->
    <div class="div-box-down"
         v-loading="loadingCols"
    >
        <form class="form-inline" >
          <div class="form-group">
            <label>label</label>
            <input
              type="text"
              class="form-control"
              style="width: 10em; margin-right: 1em;"
              v-on:keyup.13="getListPage()"
              :placeholder="start"
              v-model="colsSearch['start']"
            />

            <select class="form-control" ng-model="query" ng-change="urlChange()" >
              <option ng-repeat="item in queryUrl track by $index">{{item}}</option>
            </select>

          </div>

          <el-button  class="btn btn-primary" @click="getListPage()" >查询</el-button>
          <el-button  class="btn btn-default" @click="clearColsSearch();getListPage();" >清除</el-button>
        </form>
    </div>

    <!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
    <div :id="chartId" class="echart-big-small" style="width: 100%;height: 26em;"></div>


    <!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
    <div :id="chartId2" class="echart-big-small" style="width: 100%;height: 26em;"></div>


  </div>
</template>

<script>
import echarts from 'echarts'


export default {
  data() {
    return {
      list: [],
      colsMap: {},      //列名:别名
      colsSearch: '',   //搜索 列明:搜索值
      queryUrl: [],
      queryUrlCount: [],
      loadingList: true,
      loadingCols: true,
      chart: null,        //chart对象
      chartId: 'chartsId', //对象对应dom id
      option: {
        backgroundColor: '#394056',
        title: {
          top: 20,
          text: 'Requests',
          textStyle: {
            fontWeight: 'normal',
            fontSize: 16,
            color: '#F1F1F3'
          },
          left: '1%'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            lineStyle: {
              color: '#57617B'
            }
          }
        },
        legend: {
          top: 20,
          icon: 'rect',
          itemWidth: 14,
          itemHeight: 5,
          itemGap: 13,
          data: ['CMCC', 'CTCC', 'CUCC'],
          right: '4%',
          textStyle: {
            fontSize: 12,
            color: '#F1F1F3'
          }
        },
        grid: {
          top: 100,
          left: '2%',
          right: '2%',
          bottom: '2%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: '#57617B'
            }
          },
          data: ['13:00', '13:05', '13:10', '13:15', '13:20', '13:25', '13:30', '13:35', '13:40', '13:45', '13:50', '13:55']
        }],
        yAxis: [{
          type: 'value',
          name: '(%)',
          axisTick: {
            show: false
          },
          axisLine: {
            lineStyle: {
              color: '#57617B'
            }
          },
          axisLabel: {
            margin: 10,
            textStyle: {
              fontSize: 14
            }
          },
          splitLine: {
            lineStyle: {
              color: '#57617B'
            }
          }
        }],
        series: [{
          name: 'CMCC',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 5,
          showSymbol: false,
          lineStyle: {
            normal: {
              width: 1
            }
          },
          areaStyle: {
            normal: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 0,
                color: 'rgba(137, 189, 27, 0.3)'
              }, {
                offset: 0.8,
                color: 'rgba(137, 189, 27, 0)'
              }], false),
              shadowColor: 'rgba(0, 0, 0, 0.1)',
              shadowBlur: 10
            }
          },
          itemStyle: {
            normal: {
              color: 'rgb(137,189,27)',
              borderColor: 'rgba(137,189,2,0.27)',
              borderWidth: 12

            }
          },
          data: [220, 182, 191, 134, 150, 120, 110, 125, 145, 122, 165, 122]
        }, {
          name: 'CTCC',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 5,
          showSymbol: false,
          lineStyle: {
            normal: {
              width: 1
            }
          },
          areaStyle: {
            normal: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 0,
                color: 'rgba(0, 136, 212, 0.3)'
              }, {
                offset: 0.8,
                color: 'rgba(0, 136, 212, 0)'
              }], false),
              shadowColor: 'rgba(0, 0, 0, 0.1)',
              shadowBlur: 10
            }
          },
          itemStyle: {
            normal: {
              color: 'rgb(0,136,212)',
              borderColor: 'rgba(0,136,212,0.2)',
              borderWidth: 12

            }
          },
          data: [120, 110, 125, 145, 122, 165, 122, 220, 182, 191, 134, 150]
        }, {
          name: 'CUCC',
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 5,
          showSymbol: false,
          lineStyle: {
            normal: {
              width: 1
            }
          },
          areaStyle: {
            normal: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 0,
                color: 'rgba(219, 50, 51, 0.3)'
              }, {
                offset: 0.8,
                color: 'rgba(219, 50, 51, 0)'
              }], false),
              shadowColor: 'rgba(0, 0, 0, 0.1)',
              shadowBlur: 10
            }
          },
          itemStyle: {
            normal: {
              color: 'rgb(219,50,51)',
              borderColor: 'rgba(219,50,51,0.2)',
              borderWidth: 12
            }
          },
          data: [220, 182, 125, 145, 122, 191, 134, 150, 120, 110, 165, 122]
        }]
      },  //对应数据

      chart2: null,
      chartId2: 'chartId2',
      option2: {},

    }
  },
  created() {
    // this.getColumns()
  },
  mounted() {
    this.initChart()
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  filters: {
  },
  methods: {
    //查询展示的行列信息 备注
    getColumns() {
      this.loadingCols = true
      this.loadingCols = false
      this.getListPage()
    },
    //清空搜索条件
    clearColsSearch(){
      for (var key in this.colsMap) {
        this.colsSearch[key] = '';
      }
    },
     //分页查询
     getListPage() {
      this.loadingList = true
      // name/nowPage/showNum
      var params = {url:this.colsSearch}
      this.get('/tomcat/statics.do', params).then((data) => {
        debugger
        this.option = data.option;
        if(this.queryUrl == null){
          data.option.xAxis.data.push("");
          this.queryUrl =  data.option.xAxis.data;
        }
        this.loadingList = false
      }).catch(() => {
        this.loadingList = false
      })
       this.get('/tomcat/statics_count.do', params).then((data) => {
         debugger
         this.option = data.option;
         if(this.queryUrlCount == null){
           data.option.xAxis.data.push("");
           this.queryUrlCount =  data.option.xAxis.data;
         }
         this.toolSetChart("echarts_count", data.option);
         this.loadingList = false
       }).catch(() => {
         this.loadingList = false
       })


    },
    //修改单行 展示弹框
    handlerChange(val) {
      this.loadingUpdate = ! this.loadingUpdate
      this.loadingSave = false
      console.info("handlerChange " + JSON.stringify(val))
      this.colsChangeNow = val;
      this.colsUpdate = JSON.parse(JSON.stringify(val))
      this.loadingSave = false
    },


    initChart() {
      this.chart = echarts.init(document.getElementById(this.chartId))
      this.chart.setOption(this.option)
      this.option2 = this.option
      this.chart2 = echarts.init(document.getElementById(this.chartId2))
      this.chart2.setOption(this.option2)


    }


  }
}
</script>
