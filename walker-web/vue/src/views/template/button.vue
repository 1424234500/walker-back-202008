<template>
  <div class="app-container">
    按钮
    <template>

      <el-button  size="mini" type="primary" @click="getListPage()" >查询</el-button>
      <el-button  size="mini" type="normal" @click="clearColsSearch();getListPage();" >清除 </el-button>

    </template>






  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        name: '',
        region: '',
        date1: '',
        date2: '',
        delivery: false,
        type: [],
        resource: '',
        desc: ''
      }
    }
  },
  methods: {
    onSubmit() {
      this.$message('submit!')
    },
    onCancel() {
      this.$message({
        message: 'cancel!',
        type: 'warning'
      })
    }
  }
}
</script>

<style scoped>
.line{
  text-align: center;
}
</style>

