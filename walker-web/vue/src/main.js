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
import VueLazyload from 'vue-lazyload'
//Vue.use(VueLazyload)
// or with options
Vue.use(VueLazyload, {
  preLoad: 1.3,
  error: '/static/img/404.a57b6f31.png',
  loading: 'dist/loading.gif',
  attempt: 1
})







import mtable from './views/db/mtable'
Vue.component('mtable', mtable)

import varea from './views/user/varea'
Vue.component('varea', varea)
import areatree from './views/user/areatree'
Vue.component('areatree', areatree)

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

import ablum from './views/template/ablum'
Vue.component('ablum', ablum)



import '@/icons' // icon
import '@/permission' // permission control


//将方法挂载到Vue原型上 vue上下文 全局 this.get
import { get, post, put, delet, down, downPath } from '@/utils/http'
Vue.prototype.get = get
Vue.prototype.post = post
Vue.prototype.delet = delet
Vue.prototype.put = put
Vue.prototype.down = down //??不行？?
Vue.prototype.downPath = downPath //??不行？?

//批量挂载 工具类方法
//import { assign, clone} from '@/utils/index'
//Vue.prototype.assign = assign
//Vue.prototype.clone = clone
import * as utils from '@/utils/index'
Object.keys(utils).forEach(key => {
  Vue.prototype[key] = utils[key];
})



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



//  参数1：过滤器名称 参数2：过滤器的逻辑
import * as filters from './filters' // global filters
console.log(filters)
// register global utility filters
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})



Vue.config.productionTip = false


var v = new Vue({
  el: '#app',
  router,
  store,
  // components : components,
  render: h => h(App)
})

//debugger
