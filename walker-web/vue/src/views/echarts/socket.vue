<template>
  <div class="app-container" >


    <div>socket统计</div>

<!--搜索-->
    <div class="div-box-down"
         v-loading="loadingList"
    >
        <form class="form-inline" >
          <div class="form-group">
            <label>url</label>
            <el-select v-model="colsSearch.url"
                       clearable filterable allow-create
                       placeholder="请选择" no-match-text="新建">
              <el-option
                v-for="item in queryUrl"
                :key="item"
                :label="item"
                :value="item"
              >
              </el-option>
            </el-select>
            <label>from</label>
            <input
              type="text"
              class="form-control"
              style="width: 10em; margin-right: 1em;"
              v-on:keyup.13="getListPage()"
              placeholder="yyyy-MM-dd HH:mm"
              v-model="colsSearch.from"
            />
            <label>to</label>
            <input
              type="text"
              class="form-control"
              style="width: 10em; margin-right: 1em;"
              v-on:keyup.13="getListPage()"
              placeholder="yyyy-MM-dd HH:mm"
              v-model="colsSearch.to"
            />
          </div>

          <el-button  class="btn btn-primary" @click="getListPage()" >查询</el-button>
          <el-button  class="btn btn-danger" @click="clearColsSearch();getListPage();" >清除</el-button>
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
      colsSearch: {},      //列名:别名
      queryUrl: [],
      queryUrlCount: [],
      urlCount: "",
      loadingList: true,
      loadingCols: true,
      chart: null,        //chart对象
      chartId: 'chartsId', //对象对应dom id
      option: {},  //对应数据
      chart2: null,        //chart对象
      chartId2: 'chartsId2', //对象对应dom id
      option2: {},  //对应数据
      other: {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        toolbox: {
          show: true,
          // orient: 'vertical',
          left: 'right',
          top: 'top',
          feature: {
            mark: {show: true},
            dataView: {show: true, readOnly: false},
            magicType: {show: true, type: ['line', 'bar', 'stack', 'tiled', 'pie', 'scatter', 'radar']},
            restore: {show: true},
            saveAsImage: {show: true}
          }
        },
        calculable: true,
      }

    }
  },
  created() {
  },
  mounted() {
    this.initChart()
    this.getColumns()
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
      for (var key in this.colsSearch) {
        this.colsSearch[key] = '';
      }
    },
     //分页查询
     getListPage() {
      this.loadingList = true
      var params = this.colsSearch
      this.get('/redis/statics.do', params).then((data) => {
        data = data.data
        this.queryUrl =  data.items
        this.colsSearch = data.arg
        this.option = Object.assign(data.option, this.other)
        this.chart.setOption(this.option, true)


        this.option2 = Object.assign(data.option2, this.other)
        this.chart2.setOption(this.option2, true)

        this.loadingList = false
      }).catch(() => {
        this.loadingList = false
      })

    },

    initChart() {
      this.chart = echarts.init(document.getElementById(this.chartId))
      this.chart2 = echarts.init(document.getElementById(this.chartId2))
    },


  }
}
</script>
