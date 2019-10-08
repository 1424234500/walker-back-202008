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
    <div id="echarts_count" class="echart-big-small"></div>

    <!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
    <div id="echarts" class="echart-big-small"></div>



  </div>
</template>

<script>

export default {
  filters: {
  },
  data() {
    return {
      list: [],
      colsMap: {},      //列名:别名
      colsSearch: '',   //搜索 列明:搜索值
      loadingList: true,
      loadingCols: true,
    }
  },
  created() {
    this.getColumns()
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
      this.get('/tomcat/statics.do', params).then((res) => {
        debugger
        $scope.option = data.option;
        if($scope.queryUrl == null){
          data.option.xAxis.data.push("");
          $scope.queryUrl =  data.option.xAxis.data;
        }
        toolSetChart("echarts", data.option);
        this.loadingList = false
      }).catch(() => {
        this.loadingList = false
      })
       this.get('/tomcat/statics_count.do', params).then((res) => {
         debugger
         this.option = data.option;
         if(this.queryUrlCount == null){
           data.option.xAxis.data.push("");
           this.queryUrlCount =  data.option.xAxis.data;
         }
         toolSetChart("echarts_count", data.option);
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



  }
}
</script>
