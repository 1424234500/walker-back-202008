<template>
  <div class="dashboard-container">
    <div class="dashboard-text">name: {{ user['NAME'] }}</div>
  </div>
</template>

<script>
import { getToken,setToken,getUser,setUser } from '@/utils/store' // get token from cookie

export default {
    created() {
      this.user = getUser()


    },
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
        user: null,

      }
    },
    methods: {
      //查询展示的行列信息 备注
      getColumns() {
        this.loadingCols = true
        this.get('/common/getColsMap.do', {tableName: 'W_ROLE_USER'}).then((res) => {
          this.colMap = res.data.colMap
          this.colKey = res.data.colKey
          this.clearRowSearch()
          this.loadingCols = false
          this.getListPage()
        }).catch(() => {
          this.loadingCols = false
        })

      },
    }
  }
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
  }
  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}
</style>
