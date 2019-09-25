import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
import app from './modules/app'
import settings from './modules/settings'
import user from './modules/user'

Vue.use(Vuex)
const state = {   //放变量名

}

const store = new Vuex.Store({//放模块，state/mutations/actions,等
  modules: {
    app,
    settings,
    user
  },
  getters,
  state,
  mutations:{
    increment (value) {

    }
  }

})

export default store
// 变量
// 在methods的话就这样用
// that.$store.state.urlObject.host
// 在html直接就{{urlObject}}
// 方法：
// 使用的话直接在这个样子
// that.isLoginOut()
// 如果要传参的话就这个样子
// that.$store.dispatch("isLoginOut", res.data)


// dispatch：含有异步操作，数据提交至 actions ，可用于向后台提交数据
//
// 写法示例： this.$store.dispatch('isLogin', true);
//
// commit：同步操作，数据提交至 mutations ，可用于登录成功后读取用户信息写到缓存里
//
// 写法示例： this.$store.commit('loginStatus', 1);
