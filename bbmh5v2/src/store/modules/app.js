
export const UPDATE_APPINFO = 'UPDATE_APPINFO'
export const UPDATE_TOKEN = 'UPDATE_TOKEN'

const state = {
  appInfo: {},
  token: ''
}

// getters
const getters = {}

// actions
const actions = {
  updateAppInfo ({ commit }, app) {
    commit(UPDATE_APPINFO, app)
  },
  updateToken ({ commit }, token) {
    commit(UPDATE_TOKEN, token)
  }
}

const mutations = {
  [UPDATE_APPINFO] (state, appInfo) {
    state.appInfo = appInfo
  },
  [UPDATE_TOKEN] (state, token) {
    state.token = token
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
