import request from '@/utils/request'

// 查询文书模板列表
export function listDocTemplate(query) {
  return request({
    url: '/system/docTemplate/list',
    method: 'get',
    params: query
  })
}

// 获取模板详细
export function getDocTemplate(id) {
  return request({
    url: '/system/docTemplate/' + id,
    method: 'get'
  })
}

// 上传并解析模板
export function uploadDocTemplate(formData) {
  return request({
    url: '/system/docTemplate/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 修改模板基本信息
export function updateDocTemplate(data) {
  return request({
    url: '/system/docTemplate',
    method: 'put',
    data: data
  })
}

// 删除模板
export function delDocTemplate(id) {
  return request({
    url: '/system/docTemplate/' + id,
    method: 'delete'
  })
}

// 查询模板占位符字段列表
export function listDocTemplateFields(templateId) {
  return request({
    url: '/system/docTemplate/' + templateId + '/fields',
    method: 'get'
  })
}

// 保存模板字段映射配置
export function saveDocTemplateFields(templateId, data) {
  return request({
    url: '/system/docTemplate/' + templateId + '/fields',
    method: 'put',
    data: data
  })
}



