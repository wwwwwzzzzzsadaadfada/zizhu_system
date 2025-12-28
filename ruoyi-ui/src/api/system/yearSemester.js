import request from '@/utils/request'

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

// 获取当前学年学期
export function getCurrentYearSemester() {
  return request({
    url: '/system/yearSemester/current',
    method: 'get'
  })
}