<template>
  <div class="app-container" >


    <div>el-table-vue</div>

<!--搜索-->
    <div style="margin-top: 20px">

      <template
        v-loading="loadingCols"
        v-for="(value, key) in colsMap">
        {{value=='' ? key : value}}:

        <input
          size="small"
          :placeholder="key"
          v-model="colsSearch[key]"
        />
      </template>
      <el-button  size="mini" type="danger" @click="findPage()" >查询 </el-button>

    </div>

    <el-table
      v-loading="loadingCols || listLoading"
      :data="list"
      :row-class-name="tableRowClassName"
      :default-sort = "{prop: 'id', order: 'descending'}"
      :filters="[{text: '2016-05-01', value: '2016-05-01'}, ]"
      :filter-method="handerFilter"
      ref="multipleTable"
      @selection-change="handleSelectionChange"
      element-loading-text="Loading"
      border
      fit
      stripe
      show-summary
      highlight-current-row
      max-height="400"
    >
<!--      多选框-->
      <el-table-column
        type="selection"
        width="55">
      </el-table-column>
<!--      序号-->
      <el-table-column align="center" label="$" width="95">
        <template slot-scope="scope">
          {{ scope.$index }}
        </template>
      </el-table-column>
<!--       设置表头数据源，并循环渲染出来，property对应列内容的字段名，详情见下面的数据源格式 -->
      <el-table-column
        v-for="(value, key) in colsMap"
        :key="key"
        :property="key"
        :label="(value=='' ? key : value)"
        sortable
      >
        <template slot-scope="scope">
          {{scope.row[scope.column.property]}}  <!-- 渲染对应表格里面的内容 -->
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">修改 </el-button>

          <el-button  size="mini" type="danger" @click.native.prevent="deleteRow(scope.$index, scope.row)" >删除 </el-button>
        </template>
      </el-table-column>
    </el-table>
<!--  多选反选-->
    <div style="margin-top: 20px">
    </div>

<!--    分页-->
<!--    https://cloud.tencent.com/developer/section/1489889-->
<!--    @prev-click="handleCurrentChange"-->
<!--    @next-click="handleCurrentChange"-->
    <el-pagination
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
      :current-page="page.nowpage"
      :page-size="page.shownum"
      :page-sizes="[2, 4, 8, 16, 32, 64, 128]"
      :pager-count="11"
      layout="total, sizes, prev, pager, next, jumper"
      :total="page.num"
      background
      style="float:right;margin:10px 20px 0 0;">
    </el-pagination>

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
      colsMap: {},
      colsSearch: {},
      test:"test",
      page: {
        nowpage: 1,
        num: 0,
        order: "",
        pagenum: 0,
        shownum: 8,
      },
      listLoading: true,
      loadingCols: true,
    }
  },
  created() {
    this.fetchColumns()
  },
  methods: {
    //查询展示的行列信息 备注
    fetchColumns() {
      this.loadingCols = true
      this.post('/teacher/getColsMap.do', {tableName: 'TEACHER'}).then((res) => {
        var data = res.data

        for (var key in data) {
          this.colsMap[key.toLowerCase()] = data[key];
        }
        for (var key in this.colsMap) {
          this.colsSearch[key] = '';
        }

        console.info(this.colsSearch)

        this.loadingCols = false
        this. findPage()
      }).catch(() => {
        this.loadingCols = false
      })

    },
     //分页查询
     findPage() {
      this.listLoading = true
      // name/nowPage/showNum
       console.info(this.colsSearch)
      var params = {nowPage: this.page.nowpage, showNum: this.page.shownum}
      this.post('/teacher/findPage.do', params).then((res) => {
        this.list = res.data.data
        this.page = res.data.page
        this.listLoading = false
      }).catch(() => {
        this.listLoading = false
      })

    },
    //删除单行
    deleteRow(index, row) {
      console.info("delete")
      console.info(row)
      this.list.splice(index, 1);
    },
    //过滤器 搜索条件
    handerFilter(value, row, column) {
      const key = column['property'];
      return row[key].contains(this.colsSearch[key])
        ;
    },
    //多选改变
    handleSelectionChange(val) {
      console.info("handleSelectionChange")
      console.info(val)
      // this.multipleSelection = val;

      // this.$refs.multipleTable.toggleAllSelection()
      // this.$refs.multipleTable.toggleRowSelection(row);
    },
    //翻页
    handleCurrentChange(val) {
      console.info("current-change")
      console.info(val)
      this.page.nowpage = val
      this.findPage()
    },
    // 修改每页数量
    handleSizeChange(shownum) {
      this.page.shownum = shownum
      this. findPage()
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
