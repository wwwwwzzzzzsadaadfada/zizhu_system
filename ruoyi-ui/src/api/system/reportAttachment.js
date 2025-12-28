import request from '@/utils/request'

// 查询报表附件列表
export function listReportAttachment(query) {
  return request({
    url: '/system/report/attachment/list',
    method: 'get',
    params: query
  })
}

// 根据类型和关联ID查询附件列表
export function listAttachmentByRelated(params) {
  return request({
    url: '/system/report/attachment/listByRelated',
    method: 'get',
    params: params
  })
}

// 查询报表附件详细
export function getReportAttachment(id) {
  return request({
    url: '/system/report/attachment/' + id,
    method: 'get'
  })
}

// 上传单个附件
export function uploadAttachment(data) {
  return request({
    url: '/system/report/attachment/upload',
    method: 'post',
    data: data
  })
}

// 批量上传附件
export function uploadAttachments(data) {
  return request({
    url: '/system/report/attachment/uploads',
    method: 'post',
    data: data
  })
}

// 新增报表附件
export function addReportAttachment(data) {
  return request({
    url: '/system/report/attachment',
    method: 'post',
    data: data
  })
}

// 修改报表附件
export function updateReportAttachment(data) {
  return request({
    url: '/system/report/attachment',
    method: 'put',
    data: data
  })
}

// 删除报表附件
export function delReportAttachment(ids) {
  return request({
    url: '/system/report/attachment/' + ids,
    method: 'delete'
  })
}

// 下载附件
export function downloadAttachment(id) {
  return request({
    url: '/system/report/attachment/download/' + id,
    method: 'get',
    responseType: 'blob'
  })
}

// 统计附件数量
export function countAttachment(attachType, relatedId) {
  return request({
    url: '/system/report/attachment/count',
    method: 'get',
    params: {
      attachType,
      relatedId
    }
  })
}
