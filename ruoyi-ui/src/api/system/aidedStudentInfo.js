import request from '@/utils/request'

// 查询受助学生信息列表
export function listAidedStudentInfo(query) {
  return request({
    url: '/system/aidedStudentInfo/list',
    method: 'get',
    params: query
  })
}

// 查询受助学生信息详细
export function getAidedStudentInfo(id) {
  return request({
    url: '/system/aidedStudentInfo/' + id,
    method: 'get'
  })
}

// 新增受助学生信息
export function addAidedStudentInfo(data) {
  return request({
    url: '/system/aidedStudentInfo',
    method: 'post',
    data: data
  })
}

// 修改受助学生信息
export function updateAidedStudentInfo(data) {
  return request({
    url: '/system/aidedStudentInfo',
    method: 'put',
    data: data
  })
}

// 删除受助学生信息
export function delAidedStudentInfo(id) {
  return request({
    url: '/system/aidedStudentInfo/' + id,
    method: 'delete'
  })
}

// 同步单个学生到受助学生信息表
export function syncStudentToAidedTable(studentBaseId, academicYear, semester) {
  return request({
    url: '/system/studentRecord/syncAided/' + studentBaseId + '/' + academicYear + '/' + semester,
    method: 'post'
  })
}

// 同步所有学生到受助学生信息表
export function syncAllStudentsToAidedTable(academicYear, semester) {
  return request({
    url: '/system/studentRecord/syncAllAided/' + academicYear + '/' + semester,
    method: 'post'
  })
}

// 获取同步进度
export function getSyncProgress(academicYear, semester) {
  return request({
    url: '/system/studentRecord/syncProgress/' + academicYear + '/' + semester,
    method: 'get'
  })
}