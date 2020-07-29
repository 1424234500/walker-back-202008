import axios from 'axios'
import { Message } from 'element-ui';
import { getToken,setToken,getUser,setUser,clear } from '@/utils/cookie' // get token from cookie

import router from '@/router'
import qs from 'qs'
//
// if (process.env.NODE_ENV == 'development') {
//
//   axios.defaults.baseURL = '/api';
//
// }else if (process.env.NODE_ENV == 'info') {
//
//   axios.defaults.baseURL = 'http://v.juhe.cn';
//
// }else if (process.env.NODE_ENV == 'production') {
//
//   axios.defaults.baseURL = 'http://v.juhe.cn';
//
// }
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';

axios.defaults.timeout = 10000;

axios.interceptors.request.use(function (config) {
  // 一般在这个位置判断token是否存在
  let token = getToken()
  if (token) {
    config.headers.TOKEN = token
  }
  // else {
  //   window.location.pathname = '/login'
  // }

  return config;
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error);
});

axios.interceptors.response.use(
  response => {
    return response
  },
error => {
  if (error.response) {
    switch (error.response.status) {
      case 401:
        // 返回 401 清除token信息并跳转到登录页面
        confirm('登录信息已经过期')
        router.replace({
          path: '/login'
        })
        clear()
        location.reload()
    }
  }
  return Promise.reject(error&&error.response&&error.response.data ? error.response.data : error.response )   // 返回接口返回的错误信息
});






export function get(url, params) {
  const type = 'get'
  return new Promise((resolve, reject) => {
    before(url, params, type)
    if(url.indexOf("?") < 0){
      url += "?"
    }
    params = params == null ? {} : params
    for(var key in params){
      url += key + "=" + params[key] + "&"
    }
    if(url.endsWith("&")){
      url = url.substr(0, url.length - 1)
    }

    axios.get(url, qs.stringify(params)).then(res => {
      doRes(url, params, type, res, resolve, reject)
    }).catch(err => {
      afterReject(url, params, type, err)
      reject(err)
    })
  })
}

export function post(url, params) {
  const type = 'post'
  return new Promise((resolve, reject) => {
    before(url, params, type)
    axios.post(url, qs.stringify(params)).then(res => {
      doRes(url, params, type, res, resolve, reject)
    }).catch(err => {
      afterReject(url, params, type, err)
      reject(err)
    })
  })
}

export function put(url, params) {
  const type = 'put'
  return new Promise((resolve, reject) => {
    before(url, params, type)
    axios.put(url, qs.stringify(params)).then(res => {
      doRes(url, params, type, res, resolve, reject)
    }).catch(err => {
      afterReject(url, params, type, err)
      reject(err)
    })
  })
}

export function delet(url, params) {
  const type = 'delete'
  return new Promise((resolve, reject) => {
    before(url, params, type)
    axios.delete(url).then(res => {
      doRes(url, params, type, res, resolve, reject)
    }).catch(err => {
      afterReject(url, params, type, err)
      reject(err)
    })
  })
}

export function down(url, params, fileName) {
  const type = 'down'
  return new Promise((resolve, reject) => {
    this.$message.success('下载文件' + fileName);

    before(url, params, type)
//      axios.defaults.headers['TOKEN'] = getToken()
    axios({
      method: 'post',
      url: url, // 请求地址
      data: params, // 参数
      responseType: 'blob' // 表明返回服务器返回的数据类型
    }).then(res => {
      let blob = new Blob([res.data], {
        type: 'application/vnd.ms-excel'
      })
      if (window.navigator.msSaveOrOpenBlob) {
        navigator.msSaveBlob(blob, fileName)
      } else {
        var link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        link.download = fileName
        link.click()
        //释放内存
        window.URL.revokeObjectURL(link.href)
//        doRes(url, params, type, res, resolve, reject)

      }
    }, err => {
      debugger
      this.$message.success('下载文件失败' + fileName);
      console.log(err)
      afterReject(url, params, type, err)
      reject(err)
    })
  })
}


function doRes(url, params, type, res, resolve, reject){

  if(res){
    if(res.status == 200){
      afterResolve(url, params, type, res)
      resolve(res.data);
    }else{
      afterReject(url, params, type, res)
      reject(err)
    }
  }
}

//环绕监控  缓存设计  aop
var pre = "http.js "

function before(url, params, type){
  // console.info(pre + " before " + type + " " + url + " " + JSON.stringify(params) )

}
function afterResolve(url, params, type, res){
  // console.info(pre + " resolve " + type + " " + url + " " + JSON.stringify(params) )
  // console.info(res)
  Message({
    message: 'Done ' + res.data.info + ' ' + res.data.costTime,
    duration:1000,
    type: 'success'
  });
}
function afterReject(url, params, type, err){
  // console.info(pre + " reject  " + type + " " + url + " " + JSON.stringify(params) )
  // console.info(err)
  // Message.error(err);
  // {
  //   "timestamp":1569752060437,
  //   "status":500,
  //   "error":"Internal Server Error",
  //   "message":"Failed to invoke the method finds in the service com.walker.service.TeacherService. Tried 3 times of the providers [192.168.103.240:8095] (1/1) from the registry localhost:8096 on the consumer 192.168.103.240 using the dubbo version 2.5.3. Last error is: Failed to invoke remote method: finds, provider: dubbo://192.168.103.240:8095/com.walker.service.TeacherService2?anyhost=true&application=service-provider&check=false&dubbo=2.5.3&group=jpa&interface=com.walker.service.TeacherService&logger=log4j&methods=add,finds,get,count,update,delete&monitor=dubbo%3A%2F%2Flocalhost%3A8096%2Fcom.alibaba.dubbo.registry.RegistryService%3Fapplication%3Dservice-provider%26dubbo%3D2.5.3%26logger%3Dlog4j%26owner%3Dwalker%26pid%3D28486%26protocol%3Dregistry%26refer%3Ddubbo%253D2.5.3%2526interface%253Dcom.alibaba.dubbo.monitor.MonitorService%2526pid%253D28486%2526timestamp%253D1569465229160%26registry%3Dzookeeper%26timeout%3D30000%26timestamp%3D1569465229160&owner=walker&pid=28486&revision=1.0&side=consumer&timeout=30000&timestamp=1569465229152&version=1.0, cause: message can not send, because channel is closed . url:dubbo://192.168.103.240:8095/com.walker.service.LogService?anyhost=true&application=service-provider&check=false&codec=dubbo&dubbo=2.5.3&heartbeat=60000&interface=com.walker.service.LogService&logger=log4j&methods=saveControl,saveStatis&monitor=dubbo%3A%2F%2Flocalhost%3A8096%2Fcom.alibaba.dubbo.registry.RegistryService%3Fapplication%3Dservice-provider%26dubbo%3D2.5.3%26logger%3Dlog4j%26owner%3Dwalker%26pid%3D28486%26protocol%3Dregistry%26refer%3Ddubbo%253D2.5.3%2526interface%253Dcom.alibaba.dubbo.monitor.MonitorService%2526pid%253D28486%2526timestamp%253D1569465228348%26registry%3Dzookeeper%26timeout%3D30000%26timestamp%3D1569465228346&owner=walker&pid=28486&revision=1.0&side=consumer&timeout=30000&timestamp=1569465228303&version=1.0",
  //   "path":"/teacher/findPage.do"
  // }
  if(err  && err.response ){
    err = err.response
  }
  debugger
  if(err){
    var info = null
    if(err.status)
      info += err.status
    if(err.error)
      info += " " + err.error

    if(err.message)
      info += " " + err.message

    if(info == null)
      info = JSON.stringify(err)

    Message({
      message: 'Error ' + info,
      type: 'error'
    });
  }else{
    Message({
      message: 'Error timeout ' ,
      type: 'error'
    });
  }

}


// this.$message.warning('请选择要上传的文件！')
