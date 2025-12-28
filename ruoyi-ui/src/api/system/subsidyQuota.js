import request from '@/utils/request'

// 查询资助指标下达列表
export function listSubsidyQuota(query) {
  return request({
    url: '/system/subsidyQuota/list',
    method: 'get',
    params: query
  })
}

// 查询资助指标使用情况列表
export function listSubsidyQuotaWithUsage(query) {
  return request({
    url: '/system/subsidyQuota/listWithUsage',
    method: 'get',
    params: query
  })
}

// 查询资助指标下达详细
export function getSubsidyQuota(id) {
  return request({
    url: '/system/subsidyQuota/' + id,
    method: 'get'
  })
}

// 新增资助指标下达
export function addSubsidyQuota(data) {
  return request({
    url: '/system/subsidyQuota',
    method: 'post',
    data: data
  })
}

// 修改资助指标下达
export function updateSubsidyQuota(data) {
  return request({
    url: '/system/subsidyQuota',
    method: 'put',
    data: data
  })
}

// 删除资助指标下达
export function delSubsidyQuota(id) {
  return request({
    url: '/system/subsidyQuota/' + id,
    method: 'delete'
  })
}

// 重新校准已分配金额
export function fixQuotaAllocatedAmount() {
  return request({
    url: '/system/subsidyQuota/fixAllocatedAmount',
    method: 'put'
  })
}

// 查询可用指标明细
export function listAllocatableDetails(query) {
  return request({
    url: '/system/subsidyQuota/availableDetails',
    method: 'get',
    params: query
  })
}

// 指标额度分配到学期预算
export function allocateQuota(data) {
  return request({
    url: '/system/subsidyQuota/allocate',
    method: 'post',
    data: data
  })
}

// 指标额度批量分配到学期预算
export function allocateQuotaBatch(data) {
  return request({
    url: '/system/subsidyQuota/allocate/batch',
    method: 'post',
    data: data
  })
}

// 查询指标统计信息
export function getQuotaStatistics(query) {
  return request({
    url: '/system/subsidyQuota/statistics',
    method: 'get',
    params: query
  })
}

// 查询指标已分配的预算列表
export function listAllocatedBudgets(quotaId) {
  return request({
    url: '/system/subsidyQuota/' + quotaId + '/budgets',
    method: 'get'
  })
}

// 收回已分配的预算
export function reclaimBudgets(budgetIds) {
  return request({
    url: '/system/subsidyQuota/reclaimBudgets',
    method: 'post',
    data: budgetIds
  })
}

// 批量结转到当前学期预算
export function carryOverToBudget(data) {
  return request({
    url: '/system/subsidyQuota/carryOverToBudget',
    method: 'post',
    data: data
  })
}

// 根据学年学期ID和功能分类查询指标文号列表
export function getQuotaDocNos(yearSemesterId, functionCategory) {
  return request({
    url: '/system/subsidyQuota/quotaDocNos',
    method: 'get',
    params: {
      yearSemesterId: yearSemesterId,
      functionCategory: functionCategory
    }
  })
}

// 将来源指标的资金结转到目标指标
export function carryOverToTargetQuota(data) {
  return request({
    url: '/system/subsidyQuota/carryOverToTargetQuota',
    method: 'post',
    data: data
  })
}
