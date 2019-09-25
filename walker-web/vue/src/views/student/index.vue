<template>
  <div class="app-container" >



    <div>el-table</div>

    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"
      border
      fit
      highlight-current-row
    >
      <el-table-column align="center" label="$" width="95">
        <template slot-scope="scope">
          {{ scope.$index }}
        </template>
      </el-table-column>
      <el-table-column label="ID">
        <template slot-scope="scope">
          {{ scope.row.id }}
        </template>
      </el-table-column>
      <el-table-column label="姓名">
        <template slot-scope="scope">
          {{ scope.row.name }}
        </template>
      </el-table-column>
      <el-table-column align="center" prop="created_at" label="修改时间" width="200">
        <template slot-scope="scope">
          <i class="el-icon-time" />
          <span>{{ scope.row.time }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="handleEdit(scope.$index, scope.row)">修改
          </el-button>
          <el-button
            size="mini"
            type="danger"
            @click="handleDelete(scope.$index, scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>



    <div>my-table</div>

    <table >
    <tr v-loading="loadingCols">
      <td>$</td>
      <td v-for="(value, key) in colsMap" :key="key" >
        {{ (value=='' ? key : value) }}
      </td>
    </tr>

    <tr v-loading="listLoading" v-for="(item, index) in list" :key="index">
      <td>{{index}}</td>
      <td v-for="(value, key) in colsMap" :key="key" >
        {{ item[key.toLowerCase()]}}

      </td>
    </tr>
  </table>

<!--    <div v-loading="listLoading" style="text-align: center;margin-top: 30px;">-->
<!--      <el-text>{{page}}</el-text>-->
<!--      <el-pagination-->
<!--        background-->
<!--        layout="prev, pager, next"-->
<!--        :total="total"-->
<!--        @current-change="current_change"-->
<!--      >-->
<!--      </el-pagination>-->
<!--    </div>-->



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
      list: null,
      listLoading: true,
      loadingCols: true,
    }
  },
  created() {
    this.fetchColumns()
  },
  methods: {
    fetchColumns(){
      this.loadingCols = true
      // name/nowPage/showNum
      this.post('/teacher/getColsMap.do', {tableName:'TEACHER'}).then((res)=>{
        this.colsMap = res.data
        this.loadingCols = false
        this.fetchData()
      }).catch(()=>{
        this.loadingCols = false
      })

    },
    fetchData() {
      this.listLoading = true
      // name/nowPage/showNum
      this.post('/teacher/findPage.do', {}).then((res)=>{
        this.list = res.data.data
        this.page = res.data.page
        this.listLoading = false
      }).catch(()=>{
        this.listLoading = false
      })

      // getList().then(response => {
      //   this.list = response.data.items
      //   this.listLoading = false
      // })
    }
  }
}
</script>
