import request from '@/utils/request'

// 生成Excel报表
export function generateExcel(data) {
  return request({
    url: '/system/stReportExcel/generate',
    method: 'post',
    data: data
  })
}

// 下载Excel报表
export function downloadExcel(id) {
  return request({
    url: '/system/stReportExcel/download/' + id,
    method: 'get',
    responseType: 'blob'
  })
}

// 批量生成Excel报表
export function batchGenerateExcel(data) {
  return request({
    url: '/system/stReportExcel/batchGenerate',
    method: 'post',
    data: data
  })
}

// 查询Excel报表列表
export function listReportExcel(query) {
  return request({
    url: '/system/stReportExcel/list',
    method: 'get',
    params: query
  })
}

// 删除Excel报表
export function deleteReportExcel(id) {
  return request({
    url: '/system/stReportExcel/' + id,
    method: 'delete'
  })
}

// 批量删除Excel报表
export function deleteReportExcelByIds(ids) {
  return request({
    url: '/system/stReportExcel/deleteByIds/' + ids,
    method: 'delete'
  })
}