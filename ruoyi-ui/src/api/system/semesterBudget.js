import request from '@/utils/request'

// 查询学期预算列表
export function listSemesterBudget(query) {
  return request({
    url: '/system/semesterBudget/list',
    method: 'get',
    params: query
  })
}

// 查询学期预算项目列表（按预算项目名称去重）
export function listBudgetProjects(query) {
  return request({
    url: '/system/semesterBudget/budgetProjects',
    method: 'get',
    params: query
  })
}

// 查询学期预算经济分类列表（按经济分类去重）
export function listEconomyCategories(query) {
  return request({
    url: '/system/semesterBudget/economyCategories',
    method: 'get',
    params: query
  })
}

// 根据预算项目名称查询经济分类列表（按经济分类去重）
export function listEconomyCategoriesByProject(query) {
  return request({
    url: '/system/semesterBudget/economyCategoriesByProject',
    method: 'get',
    params: query
  })
}

// 查询学期预算详细
export function getSemesterBudget(id) {
  return request({
    url: '/system/semesterBudget/' + id,
    method: 'get'
  })
}

// 查询预算经济分类
export function getBudgetEconomyCategory(id) {
  return request({
    url: '/system/semesterBudget/economyCategory/' + id,
    method: 'get'
  })
}

// 新增学期预算
export function addSemesterBudget(data) {
  return request({
    url: '/system/semesterBudget',
    method: 'post',
    data: data
  })
}

// 修改学期预算
export function updateSemesterBudget(data) {
  return request({
    url: '/system/semesterBudget',
    method: 'put',
    data: data
  })
}

// 删除学期预算
export function delSemesterBudget(id) {
  return request({
    url: '/system/semesterBudget/' + id,
    method: 'delete'
  })
}

// 修改预算状态
export function changeSemesterBudgetStatus(id, status) {
  return request({
    url: '/system/semesterBudget/status/' + id,
    method: 'put',
    data: { status }
  })
}

// 查询预算统计
export function budgetStatistics(query) {
  return request({
    url: '/system/semesterBudget/statistics',
    method: 'get',
    params: query
  })
}