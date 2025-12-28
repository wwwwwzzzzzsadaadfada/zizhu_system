import request from '@/utils/request'

// ==================== 学制管理 ====================

// 查询学制列表
export function listSchoolPlans() {
  return request({
    url: '/system/baseconfig/schoolPlans',
    method: 'get'
  })
}

// 查询学制详细
export function getSchoolPlan(id) {
  return request({
    url: '/system/baseconfig/schoolPlan/' + id,
    method: 'get'
  })
}

// 新增学制
export function addSchoolPlan(data) {
  return request({
    url: '/system/baseconfig/schoolPlan',
    method: 'post',
    data: data
  })
}

// 修改学制
export function updateSchoolPlan(data) {
  return request({
    url: '/system/baseconfig/schoolPlan',
    method: 'put',
    data: data
  })
}

// 删除学制
export function delSchoolPlan(id) {
  return request({
    url: '/system/baseconfig/schoolPlan/' + id,
    method: 'delete'
  })
}

// ==================== 年级管理 ====================

// 查询年级列表
export function listGrades() {
  return request({
    url: '/system/baseconfig/grades',
    method: 'get'
  })
}

// 查询年级详细
export function getGrade(id) {
  return request({
    url: '/system/baseconfig/grade/' + id,
    method: 'get'
  })
}

// 新增年级
export function addGrade(data) {
  return request({
    url: '/system/baseconfig/grade',
    method: 'post',
    data: data
  })
}

// 修改年级
export function updateGrade(data) {
  return request({
    url: '/system/baseconfig/grade',
    method: 'put',
    data: data
  })
}

// 删除年级
export function delGrade(id) {
  return request({
    url: '/system/baseconfig/grade/' + id,
    method: 'delete'
  })
}

// ==================== 班级管理 ====================

// 查询班级列表
export function listClasses() {
  return request({
    url: '/system/baseconfig/classes',
    method: 'get'
  })
}

// 查询班级详细
export function getClass(classId) {
  return request({
    url: '/system/baseconfig/class/' + classId,
    method: 'get'
  })
}

// 新增班级
export function addClass(data) {
  return request({
    url: '/system/baseconfig/class',
    method: 'post',
    data: data
  })
}

// 修改班级
export function updateClass(data) {
  return request({
    url: '/system/baseconfig/class',
    method: 'put',
    data: data
  })
}

// 删除班级
export function delClass(classId) {
  return request({
    url: '/system/baseconfig/class/' + classId,
    method: 'delete'
  })
}

// ==================== 学年学期管理 ====================

// 查询学年学期列表
export function listYearSemesters(query) {
  return request({
    url: '/system/yearSemester/list',
    method: 'get',
    params: query
  })
}

// 查询学年学期详细
export function getYearSemester(id) {
  return request({
    url: '/system/yearSemester/' + id,
    method: 'get'
  })
}

// 新增学年学期
export function addYearSemester(data) {
  return request({
    url: '/system/yearSemester',
    method: 'post',
    data: data
  })
}

// 修改学年学期
export function updateYearSemester(data) {
  return request({
    url: '/system/yearSemester',
    method: 'put',
    data: data
  })
}

// 删除学年学期
export function delYearSemester(id) {
  return request({
    url: '/system/yearSemester/' + id,
    method: 'delete'
  })
}

// 设置当前学期
export function setCurrentYearSemester(id) {
  return request({
    url: '/system/yearSemester/setCurrent/' + id,
    method: 'put'
  })
}
