import Vue from 'vue'

// import 'bootstrap/dist/css/bootstrap.min.css'
// import 'bootstrap/dist/js/bootstrap.min'


import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/en' // lang i18n

import '@/styles/index.scss' // global css


import App from './App'
import store from './store'
import router from './router'

// 注册为全局组件 供直接使用
import dept from './views/user/dept'
Vue.component('dept', dept)
import depttree from './views/user/depttree'
Vue.component('depttree', depttree)
import role from './views/user/role'
Vue.component('role', role)
import roleuser from './views/user/roleuser'
Vue.component('roleuser', roleuser)
import user from './views/user/user'
Vue.component('user', user)

import controller from './views/echarts/controller'
Vue.component('controller', controller)
import socket from './views/echarts/socket'
Vue.component('socket', socket)





import '@/icons' // icon
import '@/permission' // permission control


//将方法挂载到Vue原型上
import { get, post, put, delet } from '@/utils/http'
Vue.prototype.get = get
Vue.prototype.post = post
Vue.prototype.delet = delet
Vue.prototype.put = put

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */
// import { mockXHR } from '../mock'
// if (process.env.NODE_ENV === 'production') {
//   mockXHR()
// }

// set ElementUI lang to EN
Vue.use(ElementUI, { locale })




Vue.config.productionTip = false


new Vue({
  el: '#app',
  router,
  store,
  // components : components,
  render: h => h(App)
})

