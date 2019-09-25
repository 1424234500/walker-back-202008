import axios from 'axios'

import qs from 'qs'
//
// if (process.env.NODE_ENV == 'development') {
//
//   axios.defaults.baseURL = '/api';
//
// }else if (process.env.NODE_ENV == 'debug') {
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
  return config;
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {
  // 处理响应数据
  if (response.status === 200) {
    return Promise.resolve(response);
  } else {
    return Promise.reject(response);
  }
}, function (error) {
  // 处理响应失败
  return Promise.reject(error);
});




export function get(url, params) {
  before(url, params, 'get')
  return new Promise((resolve, reject) => {
    axios.get(url, {
      params: params
    }).then(res => {
      afterResolve(url, params, 'get', res)
      resolve(res.data);
    }).catch(err => {
      afterReject(url, params, 'get', err)
      reject(err.data)
    })
  });
}

export function post(url, params) {
  return new Promise((resolve, reject) => {
    before(url, params, 'post')
    axios.post(url, qs.stringify(params))
      .then(res => {
      afterResolve(url, params, 'post', res)
      resolve(res.data);
    }).catch(err => {
      afterReject(url, params, 'post', err)
      reject(err.data)
    })
}

var pre = "http.js "
function before(url, params, type){
  console.info(pre + " before " + type + " " + url + " " + JSON.stringify(params) )

}
function afterResolve(url, params, type, res){
  console.info(pre + " afterResolve " + type + " " + url + " " + JSON.stringify(params) )
  console.info(res)

}
function afterReject(url, params, type, err){
  console.info(pre + " afterReject " + type + " " + url + " " + JSON.stringify(params) )
  console.info(err)


}
