import request from '@/utils/request'

// 查询指标附件列表
export function listQuotaAttachment(query) {
  return request({
    url: '/system/quotaAttachment/list',
    method: 'get',
    params: query
  })
}

// 根据指标ID查询附件列表
export function listQuotaAttachmentByQuotaId(quotaId) {
  return request({
    url: '/system/quotaAttachment/listByQuotaId/' + quotaId,
    method: 'get'
  })
}

// 查询指标附件详细
export function getQuotaAttachment(id) {
  return request({
    url: '/system/quotaAttachment/' + id,
    method: 'get'
  })
}

// 新增指标附件
export function addQuotaAttachment(data) {
  return request({
    url: '/system/quotaAttachment',
    method: 'post',
    data: data
  })
}

// 修改指标附件
export function updateQuotaAttachment(data) {
  return request({
    url: '/system/quotaAttachment',
    method: 'put',
    data: data
  })
}

// 删除指标附件
export function delQuotaAttachment(id) {
  return request({
    url: '/system/quotaAttachment/' + id,
    method: 'delete'
  })
}

// 上传指标附件
export function uploadQuotaAttachment(quotaId, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/system/quotaAttachment/upload/' + quotaId,
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: formData
  })
}

// 下载指标附件
export function downloadQuotaAttachment(id) {
  return request({
    url: '/system/quotaAttachment/download/' + id,
    method: 'get',
    responseType: 'blob'
  })
}

// 预览指标附件
export function previewQuotaAttachment(id) {
  return request({
    url: '/system/quotaAttachment/preview/' + id,
    method: 'get'
  })
}

// 异步预览指标附件
export function previewQuotaAttachmentAsync(id) {
  return request({
    url: '/system/quotaAttachment/previewAsync/' + id,
    method: 'get'
  })
}


