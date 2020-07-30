import Vue from 'vue'
import { USER_INFO } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
export const UPDATE_USERINFO = 'UPDATE_USERINFO'
export const UPDATE_USERACCOUNTINFO = 'UPDATE_USERACCOUNTINFO'
export const UPDATE_USEUNREADMSGCOUNT = 'UPDATE_USEUNREADMSGCOUNT'
export const UPDATE_RESET_MSGCOUNT = 'UPDATE_RESET_MSGCOUNT'

const state = {
  userInfo: {},
  userAccountInfo: {}
}

// getters
const getters = {}

// actions
const actions = {
  getUserInfo ({ commit }) {
    const { tokenBbm } = getLocalStorage('tokenBbm')
    Vue.prototype.$reqPost(USER_INFO, { token: tokenBbm, sign: Vue.prototype.$getJsaParam({ token: tokenBbm }) }).then(res => {
      if (res.status.code === 200) {
        commit(UPDATE_USERINFO, res.data.userInfo)
        commit(UPDATE_USERACCOUNTINFO, res.data.userAccountInfo)
      }
    }).catch(err => {
      console.log(err)
    })
  },
  decreaseUserUnReadMsgCount ({ commit }) {
    commit(UPDATE_USEUNREADMSGCOUNT)
  },
  resetUnReadMsgCount ({ commit }) {
    commit(UPDATE_RESET_MSGCOUNT)
  }
}

const mutations = {
  [UPDATE_USERINFO] (state, userInfo) {
    state.userInfo = userInfo
  },
  [UPDATE_USERACCOUNTINFO] (state, userAccountInfo) {
    state.userAccountInfo = userAccountInfo
  },
  [UPDATE_USEUNREADMSGCOUNT] (state) {
    if (state.userInfo.notReadNotificationNum > 0) { // 未读消息减1
      state.userInfo.notReadNotificationNum -= 1
    }
  },
  [UPDATE_RESET_MSGCOUNT] (state) {
    if (state.userInfo.notReadNotificationNum > 0) { // 未读消息重置为0
      state.userInfo.notReadNotificationNum = 0
    }
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
