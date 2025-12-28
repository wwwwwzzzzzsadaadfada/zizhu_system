import request from '@/utils/request'

// 查询学生学期信息列表
export function listStudentSemester(query) {
  return request({
    url: '/system/studentSemester/list',
    method: 'get',
    params: query
  })
}

// 查询学生学期信息详细
export function getStudentSemester(id) {
  return request({
    url: '/system/studentSemester/' + id,
    method: 'get'
  })
}

// 新增学生学期信息
export function addStudentSemester(data) {
  return request({
    url: '/system/studentSemester',
    method: 'post',
    data: data
  })
}

// 修改学生学期信息
export function updateStudentSemester(data) {
  return request({
    url: '/system/studentSemester',
    method: 'put',
    data: data
  })
}

// 删除学生学期信息
export function delStudentSemester(id) {
  return request({
    url: '/system/studentSemester/' + id,
    method: 'delete'
  })
}
