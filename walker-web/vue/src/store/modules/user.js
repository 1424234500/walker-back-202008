// import { login, logout, getInfo } from '@/api/shiro'
import { getToken,setToken,getUser,setUser } from '@/utils/store' // get token from cookie
import { resetRouter } from '@/router'
import { get, post } from '@/utils/http'

//属性私有?
const state = {
  token: getToken(),
  name: '',
  avatar: ''
}
//数据操作函数set?
const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  }
}
//other异步 方法?
const actions = {
  loginin({ commit, state }, data){
    console.info("module/user/loginOn设置当前用户信息 ", data)
    const token = data['TOKEN']
    const name = data['USER']['NAME']
    commit('SET_TOKEN', token)
    commit('SET_NAME', name)
    setToken(token)
    return new Promise((resolve, reject) => {
      resolve(token)
    })
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      resolve({name:state.name, avatar:stat.avatar})
    })
  },

  // user logout
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        commit('SET_TOKEN', '')
        setToken('')
        resetRouter()
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      setToken('')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

