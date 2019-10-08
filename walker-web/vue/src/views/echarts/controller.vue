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
          </div>

          <el-button  class="btn btn-primary" @click="getListPage()" >查询</el-button>
          <el-button  class="btn btn-default" @click="clearColsSearch();getListPage();" >清除</el-button>
        </form>
    </div>



  </div>
</template>

<script>
// import { getList } from '@/api/table'

export default {
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'gray',
        deleted: 'danger'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      list: [],
      colsMap: {},      //列名:别名
      colsSearch: {},   //搜索 列明:搜索值
      colsUpdate: {},   //更新界面复制 列名:新值
      colsChangeNow: {},//更新界面源对象 列名:旧值
      test:"test",
      page: {
        nowpage: 1,
        num: 0,
        order: "",
        pagenum: 0,
        shownum: 8,
      },
      loadingList: true,
      loadingCols: true,
      loadingSave: true,
      loadingUpdate: false,
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
      this.post('/teacher/getColsMap.do', {tableName: 'TEACHER'}).then((res) => {
        var data = res.data

        for (var key in data) {
          this.colsMap[key.toLowerCase()] = data[key];
        }
        this.clearColsSearch()

        console.info(this.colsSearch)

        this.loadingCols = false
        this. getListPage()
      }).catch(() => {
        this.loadingCols = false
      })

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
      var params = Object.assign({nowPage: this.page.nowpage, showNum: this.page.shownum, order: this.page.order}, this.colsSearch)
      this.post('/teacher/findPage.do', params).then((res) => {
        this.list = res.data.data
        this.page = res.data.page
        this.loadingList = false
      }).catch(() => {
        this.loadingList = false
      })

    },
    //添加行
    handlerAddColumn(){
      this.list.push({})
      this.handlerChange(this.list[this.list.length - 1])
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
    //取消修改  不做操作
    handlerCancel(){
      console.info("handlerCancel "+ JSON.stringify(this.colsUpdate))
      this.loadingUpdate = ! this.loadingUpdate
    },
    //保存修改  保存至表格 和 数据库 ? 还是批量改完后一次存储 先临时选中修改过的
    handlerSave(){
      console.info("handlerSave "+ JSON.stringify(this.colsUpdate))
      this.loadingSave = true

      Object.assign(this.colsChangeNow, this.colsUpdate)
      var params = this.colsChangeNow
      this.put('/teacher/action.do', params).then((res) => {
        this.loadingSave = false
        this.loadingUpdate = ! this.loadingUpdate
      }).catch(() => {
        this.loadingSave = false
        this.loadingUpdate = ! this.loadingUpdate
      })

    },
    //删除单行
    handlerDelete(index, val) {
      console.info("handlerDelete " + index + " " + JSON.stringify(val))
      this.loadingList = true
      var params = {}
      this.delet('/teacher/'+val.id+'/action.do', params).then((res) => {
        this.loadingList = false
        this.list.splice(index, 1);
      }).catch(() => {
        this.loadingList = false
      })


    },
    //多选改变
    handlerSelectionChange(val) {
      console.info("handlerSelectionChange" + JSON.stringify(val))
      console.info(val)
      // this.multipleSelection = val;

      // this.$refs.multipleTable.toggleAllSelection()
      // this.$refs.multipleTable.toggleRowSelection(VAL);
    },
    //排序事件
    handlerSortChange(val) {
      console.info("handlerSortChange " + JSON.stringify(val))
      // column: {…}
      // order: "ascending"
      // prop: "time"
      this.page.order = val.prop + (val.order.startsWith("desc") ? " desc" : "")

    },
    //翻页
    handlerCurrentChange(val) {
      console.info("handlerCurrentChange")
      console.info(val)
      this.page.nowpage = val
      this.getListPage()
    },
    // 修改每页数量
    handlerSizeChange(shownum) {
      this.page.shownum = shownum
      this. getListPage()
    },
    //行高亮各种颜色
    tableRowClassName({row, rowIndex}) {
      if (rowIndex === 1) {
        return 'warning-row';
      } else if (rowIndex === 3) {
        return 'success-row';
      }
      return '';
    },
  }
}
</script>
