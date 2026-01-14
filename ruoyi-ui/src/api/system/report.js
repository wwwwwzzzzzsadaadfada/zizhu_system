import request from '@/utils/request'

// 查询报表列表
export function listReport(query) {
  return request({
    url: '/system/report/list',
    method: 'get',
    params: query
  })
}

// 查询报表树（后端已按学段分组）
export function listReportTree(query) {
  return request({
    url: '/system/report/tree',
    method: 'get',
    params: query
  })
}

// 查询报表详细
export function getReport(id) {
  return request({
    url: '/system/report/' + id,
    method: 'get'
  })
}

// 检查报表类型（后端判断是否需要学生）
export function checkReportType(reportId) {
  return request({
    url: '/system/report/checkReportType/' + reportId,
    method: 'get'
  })
}

// 更新报表排序
export function updateReportSort(data) {
  return request({
    url: '/system/report/updateSort',
    method: 'post',
    data: data
  })
}

// 获取助学金申请表URL（后端处理所有逻辑）
export function getSubsidyReportUrl(studentId) {
  return request({
    url: '/system/report/getSubsidyReportUrl',
    method: 'get',
    params: { studentId }
  })
}


