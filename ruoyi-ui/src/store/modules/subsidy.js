const state = {
  shouldRefreshSubsidyList: false,
  shouldRefreshBudgetList: false
}

const mutations = {
  SET_SHOULD_REFRESH_SUBSIDY_LIST: (state, shouldRefresh) => {
    state.shouldRefreshSubsidyList = shouldRefresh
  },
  SET_SHOULD_REFRESH_BUDGET_LIST: (state, shouldRefresh) => {
    state.shouldRefreshBudgetList = shouldRefresh
  }
}

const actions = {
  setShouldRefreshSubsidyList({ commit }, shouldRefresh) {
    commit('SET_SHOULD_REFRESH_SUBSIDY_LIST', shouldRefresh)
  },
  setShouldRefreshBudgetList({ commit }, shouldRefresh) {
    commit('SET_SHOULD_REFRESH_BUDGET_LIST', shouldRefresh)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}