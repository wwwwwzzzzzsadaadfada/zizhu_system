import request from '@/utils/request'

// 查询资助政策列表
export function listSubsidyPolicy(query) {
  return request({
    url: '/system/subsidyPolicy/list',
    method: 'get',
    params: query
  })
}

// 查询资助政策详细
export function getSubsidyPolicy(id) {
  return request({
    url: '/system/subsidyPolicy/' + id,
    method: 'get'
  })
}

// 查询有效政策列表
export function getEffectivePolicies(schoolingPlanId, yearSemesterId) {
  return request({
    url: '/system/subsidyPolicy/effective',
    method: 'get',
    params: {
      schoolingPlanId: schoolingPlanId,
      yearSemesterId: yearSemesterId
    }
  })
}

// 新增资助政策
export function addSubsidyPolicy(data) {
  return request({
    url: '/system/subsidyPolicy',
    method: 'post',
    data: data
  })
}

// 修改资助政策
export function updateSubsidyPolicy(data) {
  return request({
    url: '/system/subsidyPolicy',
    method: 'put',
    data: data
  })
}

// 删除资助政策
export function delSubsidyPolicy(id) {
  return request({
    url: '/system/subsidyPolicy/' + id,
    method: 'delete'
  })
}

// 发布政策
export function publishPolicy(id) {
  return request({
    url: '/system/subsidyPolicy/publish/' + id,
    method: 'put'
  })
}

// 废止政策
export function abolishPolicy(id, abolishReason) {
  return request({
    url: '/system/subsidyPolicy/abolish/' + id,
    method: 'put',
    params: {
      abolishReason: abolishReason
    }
  })
}

// 导出资助政策
export function exportSubsidyPolicy(query) {
  return request({
    url: '/system/subsidyPolicy/export',
    method: 'post',
    params: query
  })
}


