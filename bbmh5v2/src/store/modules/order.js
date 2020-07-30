
export const UPDATE_ORDERINFO = 'UPDATE_ORDERINFO'

const state = {
  orderInfo: {}
}

// getters
const getters = {}

// actions
const actions = {
  updateOrderInfo ({ commit }, app) {
    commit(UPDATE_ORDERINFO, app)
  }
}

const mutations = {
  [UPDATE_ORDERINFO] (state, orderInfo) {
    state.orderInfo = orderInfo
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
