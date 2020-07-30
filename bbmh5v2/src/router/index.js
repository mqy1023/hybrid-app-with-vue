import Vue from 'vue'
import Router from 'vue-router'
import axios from 'axios'
import store from '../store'

import home from './home'
import dispatch from './dispatch'
import game from './game'
import user from './user'
import login from './login'
import outside from './outside'
import { clearRequest } from '../plugins/reqAxios'
import { getLocalStorage } from '@utils/local-storage'

// console.log('clearRequest', clearRequest)

// Uncaught (in promise) undefined
// https://github.com/vuejs/vue-router/issues/2881
// const originalPush = Router.prototype.push
// Router.prototype.push = function push (location, onResolve, onReject) {
//   if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
//   try {
//     return originalPush.call(this, location).catch(err => err)
//   } catch (error) {
//     // console.log(error)
//   }
// }

Vue.use(Router)

const RouterModel = new Router({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes: [...home, ...dispatch, ...game, ...user, ...login, ...outside]
})

RouterModel.beforeEach((to, from, next) => {
  // 切换路由时清空上个路由未完成的所有请求
  const CancelToken = axios.CancelToken
  clearRequest.source.cancel && clearRequest.source.cancel()
  clearRequest.source = CancelToken.source()

  const query = to.query
  if (query.version && query.src) { // http://localhost:8106/#/?version=1.0.0&src=1 传参
    let params = {}
    params.version = query.version
    params.src = query.src
    params.model = query.model
    params.jgid = query.jgid
    store.dispatch('updateAppInfo', params)
  }

  const { tokenBbm } = getLocalStorage(
    'tokenBbm'
  )
  if (!tokenBbm && to.name && to.name.indexOf('login') < 0) {
    if (to.meta.login) { // 第二版必须登录
      next({ name: 'login', query: { redirect: to.path } })
      return
    }
  }
  next()
})

export default RouterModel
