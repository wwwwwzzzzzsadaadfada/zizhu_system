import request from '@/utils/request'

// 查询学生补助发放记录列表
export function listSubsidyRecord(query) {
  return request({
    url: '/system/subsidyRecord/list',
    method: 'get',
    params: query
  })
}

// 查询学生补助发放记录详细
export function getSubsidyRecord(id) {
  return request({
    url: '/system/subsidyRecord/' + id,
    method: 'get'
  })
}

// 新增学生补助发放记录
export function addSubsidyRecord(data) {
  return request({
    url: '/system/subsidyRecord',
    method: 'post',
    data: data
  })
}

// 修改学生补助发放记录
export function updateSubsidyRecord(data) {
  return request({
    url: '/system/subsidyRecord',
    method: 'put',
    data: data
  })
}

// 删除学生补助发放记录
export function delSubsidyRecord(id) {
  return request({
    url: '/system/subsidyRecord/' + id,
    method: 'delete'
  })
}

// 审批补助
export function approveSubsidy(id, approvalStatus, approvalMemo) {
  return request({
    url: '/system/subsidyRecord/approve/' + id,
    method: 'put',
    params: {
      approvalStatus: approvalStatus,
      approvalMemo: approvalMemo
    }
  })
}

// 退回补助
export function returnSubsidy(id, returnMemo) {
  return request({
    url: '/system/subsidyRecord/return/' + id,
    method: 'put',
    params: {
      returnMemo: returnMemo
    }
  })
}

// 批量发放补助
export function batchPayment(data) {
  return request({
    url: '/system/subsidyRecord/batchPayment',
    method: 'post',
    data: data
  })
}

// Excel导入批量发放（注意：这个接口使用form-data格式，不使用request工具）
// 实际使用el-upload组件直接上传，不需要单独封装

// 下载批量发放导入模板
export function importTemplate() {
  return request({
    url: '/system/subsidyRecord/importTemplate',
    method: 'post',
    responseType: 'blob'
  })
}