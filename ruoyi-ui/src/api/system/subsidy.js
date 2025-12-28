import request from '@/utils/request'

// 查询补助套餐
export function getSubsidyPackages(params) {
  return request({
    url: '/system/subsidy/packages',
    method: 'get',
    params
  })
}

// 查询当前学期可用预算
export function getAvailableBudgets(params) {
  return request({
    url: '/system/subsidy/budgets',
    method: 'get',
    params
  })
}

// 查询历史结余预算
export function getHistoricalBudgets(params) {
  return request({
    url: '/system/subsidy/budgets/history',
    method: 'get',
    params
  })
}

// 录入补助
export function createSubsidy(data) {
  return request({
    url: '/system/subsidy/create',
    method: 'post',
    data
  })
}



