import request from '@/utils/request'

// 查询学生受助明细列表
export function listSubsidyDetail(query) {
  return request({
    url: '/system/subsidyDetail/list',
    method: 'get',
    params: query
  })
}

// 查询学生受助明细详细
export function getSubsidyDetail(id) {
  return request({
    url: '/system/subsidyDetail/' + id,
    method: 'get'
  })
}

// 新增学生受助明细
export function addSubsidyDetail(data) {
  return request({
    url: '/system/subsidyDetail',
    method: 'post',
    data: data
  })
}

// 修改学生受助明细
export function updateSubsidyDetail(data) {
  return request({
    url: '/system/subsidyDetail',
    method: 'put',
    data: data
  })
}

// 删除学生受助明细
export function delSubsidyDetail(id) {
  return request({
    url: '/system/subsidyDetail/' + id,
    method: 'delete'
  })
}