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

import '@/icons' // icon
import '@/permission' // permission control


//将方法挂载到Vue原型上
// import { get, post } from './utils/http'
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
import { mockXHR } from '../mock'
if (process.env.NODE_ENV === 'production') {
  mockXHR()
}

// set ElementUI lang to EN
Vue.use(ElementUI, { locale })

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})



Vue.filter( 'formatTime' , function(timeStamp,isData) {
  if(parseInt(timeStamp) < 11111111111){
    return timeStamp
  }
  var date = new Date();
  date.setTime(timeStamp * 1);  //1000
  var y = date.getFullYear();
  var m = date.getMonth() + 1;
  m = m < 10 ? ('0' + m) : m;
  var d = date.getDate();
  d = d < 10 ? ('0' + d) : d;
  var h = date.getHours();
  h = h < 10 ? ('0' + h) : h;
  var minute = date.getMinutes();
  var second = date.getSeconds();
  minute = minute < 10 ? ('0' + minute) : minute;
  second = second < 10 ? ('0' + second) : second;
  if (isData){
    return y + '-' + m + '-' + d;
  } else{
    return y + '-' + m + '-' + d+' '+h+':'+minute+':'+second;
  }
});
