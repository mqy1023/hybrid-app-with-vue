import Vue from 'vue'
import axios from 'axios'
import qs from 'qs'
import { Dialog } from 'vant'
import store from '@/store'
import { removeLocalStorage, getLocalStorage } from '@utils/local-storage'

import router from '../router'

const instance = axios.create({
  timeout: 24000,
  baseURL: process.env.NODE_ENV === 'development' ? '/api' : '/api'
})

// 定义全局变量clearRequest，在route.js中要用到
export const clearRequest = {
  source: {
    token: null,
    cancel: null
  }
}
const cancelToken = axios.CancelToken
const source = cancelToken.source()

instance.interceptors.request.use(
  config => {
    if (config.method === 'post' || config.method === 'put') {
      config.data = qs.stringify(config.data)
    }
    if (!config.headers.Authorization) {
      const { jgId } = getLocalStorage('jgId')
      config.headers['Authorization'] = `Bearer ${window.localStorage.getItem(
        'tokenBbm'
      ) || ''}`
      config.headers['OS'] = Vue.prototype.$isAndroid() ? '0' : '1' // 安卓:0 ios:1
      config.headers['Source'] = store.state.app.appInfo.src || '1' // 用户来源编号
      config.headers['MobileModel'] = store.state.app.appInfo.model || 'ios' // 手机型号
      config.headers['AppVersion'] = store.state.app.appInfo.version || '1.0.0' // 版本号
      config.headers['Device'] = store.state.app.appInfo.jgid || jgId || '' // 极光返回的deviceId
    }

    // 在发送请求设置cancel token
    config.cancelToken = clearRequest.source.token // 这句很重要

    return config
  },
  err => Promise.reject(err)
)

instance.interceptors.response.use(
  res => {
    Vue.prototype.$vLoading.hide()
    const resCode = res.data.status.code
    if (resCode !== 200 && resCode !== 4007 && resCode !== 6020 && resCode !== 4108 && resCode !== 4109 && resCode !== 4111 && resCode !== 4106 && resCode !== 4107 && resCode !== 6017 && resCode !== 4036 && resCode !== 6014 && resCode !== 4103 && resCode !== 4104 && resCode !== 4105 && resCode !== 4027 && resCode !== 3001 && resCode !== 4032 && resCode !== 4024 && resCode !== 4020 && resCode !== 4101 && resCode !== 4100 && resCode !== 4026 && resCode !== 5067 && resCode !== 5082 && resCode !== 4008) {
      switch (resCode) {
        case 422: {
          const flag = Array.isArray(res.data.data) && res.data.data.length
          Dialog.alert({
            message: flag ? res.data.data[0].message : res.data.message
          })
          break
        }
        case 4001: // 未登录
          removeLocalStorage('tokenBbm') // 清除原来的token的值
          store.dispatch('updateToken', '') // 更新store中token的值
          const preRouter = window.location.hash.substring(1) // 截取hash的路由  todo
          // 刚进来时请求用户信息接口可能token过期。首页/玩法页不跳转登录页
          if (preRouter == '/home' || preRouter == '/game' || preRouter.indexOf('/home?version') > -1 || preRouter.indexOf('/game?version') > -1) {
          } else {
            router.replace({ path: '/login', query: { redirect: preRouter } })
          }
          break
        case 404:
          break
        default:
          break
      }
      Vue.prototype.$toast(res.data.status.message)
      return Promise.reject(res)
    }
    return res.data
  },
  error => {
    let errMsg = '请重试~'
    if (error.message && error.message.indexOf('timeout') > -1) {
      errMsg = '请求超时，请重试~'
    } else if (error.message && error.message.indexOf('Network') > -1) {
      errMsg = '网络异常，请重试~'
    }
    if (error.message && error.message.indexOf('Cancel') < 0) { // cancel的请求不弹框
      Dialog.alert({ // error.message
        title: '提示',
        message: errMsg
      })
    }
    Vue.prototype.$vLoading.hide()
    return Promise.reject(error)
  }
)

const post = (url, data, config = {}) => instance.post(url, data,
  {
    ...config, cancelToken: source.token // 这句很重要
  })

const put = (url, data, config = {}) => instance.put(url, data, config)

const get = (url, params, config = {}) =>
  instance.get(url, {
    params,
    ...config,
    cancelToken: source.token // 这句很重要
  })

const deleteMethod = (url, config = {}) =>
  instance({
    url,
    method: 'delete',
    ...config
  })

export default {
  install (Vue) {
    Vue.prototype.$reqGet = get
    Vue.prototype.$reqPost = post
    Vue.prototype.$reqPut = put
    Vue.prototype.$reqDel = deleteMethod
  }
}
