import Cookies from 'js-cookie'

const TokenKey = 'vue_admin_template_token'

export function getToken() {
  console.info("utils/auth.js/gettoken")
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  console.info("utils/auth.js/settoken")
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}
