import request from '@/utils/request'

// 查询学生学期记录列表
export function listStudentRecords(query) {
  return request({
    url: '/system/studentRecord/list',
    method: 'get',
    params: query
  })
}

// 查询学生学期记录详细
export function getStudentRecord(id) {
  return request({
    url: '/system/studentRecord/' + id,
    method: 'get'
  })
}

// 新增学生学期记录
export function addStudentRecord(data) {
  return request({
    url: '/system/studentRecord',
    method: 'post',
    data: data
  })
}

// 修改学生学期记录
export function updateStudentRecord(data) {
  return request({
    url: '/system/studentRecord',
    method: 'put',
    data: data
  })
}

// 删除学生学期记录
export function delStudentRecord(id) {
  return request({
    url: '/system/studentRecord/' + id,
    method: 'delete'
  })
}

// 批量删除学生学期记录
export function delStudentRecords(ids) {
  return request({
    url: '/system/studentRecord/' + ids,
    method: 'delete'
  })
}

// 导出学生学期记录
export function exportStudentRecord(query) {
  return request({
    url: '/system/studentRecord/export',
    method: 'get',
    params: query
  })
}

// 查询学生基础信息列表（用于选择）
export function listStudentsBase(query) {
  return request({
    url: '/system/studentsBase/list',
    method: 'get',
    params: query
  })
}

// 查询学生基础信息详细
export function getStudentsBase(id) {
  return request({
    url: '/system/studentsBase/' + id,
    method: 'get'
  })
}

// 新增学生基础信息
export function addStudentsBase(data) {
  return request({
    url: '/system/studentsBase',
    method: 'post',
    data: data
  })
}

// 修改学生基础信息
export function updateStudentsBase(data) {
  return request({
    url: '/system/studentsBase',
    method: 'put',
    data: data
  })
}

// 查询学制列表
export function getSchoolPlanList() {
  return request({
    url: '/system/baseconfig/schoolPlans',
    method: 'get'
  })
}

// 查询年级列表
export function getGradeList(schoolingPlanId) {
  return request({
    url: '/system/baseconfig/grades',
    method: 'get',
    params: { schoolingPlanId }
  })
}

// 查询班级列表
export function getClassList(gradeId) {
  return request({
    url: '/system/baseconfig/classes',
    method: 'get',
    params: { gradeId }
  })
}

// 同步学生数据到学期记录
export function syncStudents(yearSemesterId) {
  return request({
    url: '/system/studentRecord/sync/' + yearSemesterId,
    method: 'post'
  })
}


