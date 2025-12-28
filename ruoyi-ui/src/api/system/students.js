import request from '@/utils/request'

// 查询困难学生基础信息列表
export function listStudents(query) {
  return request({
    url: '/system/students/list',
    method: 'get',
    params: query
  })
}

// 查询困难学生基础信息详细
export function getStudents(id) {
  return request({
    url: '/system/students/' + id,
    method: 'get'
  })
}

// 新增困难学生基础信息
export function addStudents(data) {
  return request({
    url: '/system/students',
    method: 'post',
    data: data
  })
}

// 修改困难学生基础信息
export function updateStudents(data) {
  return request({
    url: '/system/students',
    method: 'put',
    data: data
  })
}

// 删除困难学生基础信息
export function delStudents(id) {
  return request({
    url: '/system/students/' + id,
    method: 'delete'
  })
}

// 获取学制列表
export function getSchoolPlanList() {
  return request({
    url: '/system/students/schoolPlans',
    method: 'get'
  })
}

// 根据学制ID获取年级列表
export function getGradeList(schoolingPlanId) {
  return request({
    url: '/system/students/grades/' + schoolingPlanId,
    method: 'get'
  })
}

// 根据年级ID获取班级列表
export function getClassList(gradeId) {
  return request({
    url: '/system/students/classes/' + gradeId,
    method: 'get'
  })
}

// 批量更新困难类型和等级
export function batchUpdateDifficulty(data) {
  return request({
    url: '/system/students/batchUpdateDifficulty',
    method: 'put',
    data: data
  })
}