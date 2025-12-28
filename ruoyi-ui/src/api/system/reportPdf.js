import request from '@/utils/request'

// 查询报表PDF记录列表
export function listReportPdf(query) {
  return request({
    url: '/system/report/pdf/list',
    method: 'get',
    params: query
  })
}

// 查询文件包列表（按reportId分组）
export function listPackages(query) {
  return request({
    url: '/system/report/pdf/packages',
    method: 'get',
    params: query
  })
}

// 查询可用报表列表（用于合并PDF）
export function listAvailableReports(query) {
  return request({
    url: '/system/report/pdf/availableReports',
    method: 'get',
    params: query
  })
}

// 查询报表PDF记录详细
export function getReportPdf(id) {
  return request({
    url: '/system/report/pdf/' + id,
    method: 'get'
  })
}

// 生成并保存单个学生报表PDF
export function generatePdf(data) {
  return request({
    url: '/system/report/pdf/generate',
    method: 'post',
    data: data
  })
}

// 批量生成并保存学生报表PDF
export function batchGeneratePdf(data) {
  return request({
    url: '/system/report/pdf/batchGenerate',
    method: 'post',
    data: data
  })
}

// 合并PDF文件
export function mergePdfs(data) {
  return request({
    url: '/system/report/pdf/merge',
    method: 'post',
    data: data
  })
}

// 按条件批量合并PDF
export function mergePdfsByCondition(data) {
  return request({
    url: '/system/report/pdf/mergeByCondition',
    method: 'post',
    data: data
  })
}

// 下载PDF文件
export function downloadPdf(id) {
  return request({
    url: '/system/report/pdf/download/' + id,
    method: 'get',
    responseType: 'blob'
  })
}

// 删除报表PDF记录
export function delReportPdf(id) {
  return request({
    url: '/system/report/pdf/' + id,
    method: 'delete'
  })
}

// 更新报表PDF记录
export function updateReportPdf(data) {
  return request({
    url: '/system/report/pdf',
    method: 'put',
    data: data
  })
}

// 批量删除报表PDF记录
export function delReportPdfs(ids) {
  return request({
    url: '/system/report/pdf/' + ids,
    method: 'delete'
  })
}

// 删除文件包（按reportId删除该报表的所有PDF归档）
export function delPackageByReportId(reportId) {
  return request({
    url: '/system/report/pdf/package/' + reportId,
    method: 'delete'
  })
}

// 查询PDF合并批次列表
export function listReportPdfBatch(query) {
  return request({
    url: '/system/report/pdf/batch/list',
    method: 'get',
    params: query
  })
}

// 查询PDF合并批次详细
export function getReportPdfBatch(id) {
  return request({
    url: '/system/report/pdf/batch/' + id,
    method: 'get'
  })
}

// 下载合并后的PDF文件
export function downloadBatchPdf(id) {
  return request({
    url: '/system/report/pdf/batch/download/' + id,
    method: 'get',
    responseType: 'blob'
  })
}




