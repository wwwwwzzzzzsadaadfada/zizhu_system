import request from '@/utils/request'

// 获取控制舱所有统计数据
export function getDashboardData(segment = 'all') {
  return request({
    url: '/dashboard/data',
    method: 'get',
    params: { segment }
  })
}

// 获取顶部统计数据
export function getTopStats() {
  return request({
    url: '/dashboard/topStats',
    method: 'get'
  })
}

// 获取困难类型分布
export function getDifficultyTypeDistribution() {
  return request({
    url: '/dashboard/difficultyTypeDistribution',
    method: 'get'
  })
}

// 获取各学段受助人数
export function getSchoolLevelStats() {
  return request({
    url: '/dashboard/schoolLevelStats',
    method: 'get'
  })
}

// 获取指标执行情况
export function getQuotaExecution() {
  return request({
    url: '/dashboard/quotaExecution',
    method: 'get'
  })
}

// 获取地图数据
export function getMapData() {
  return request({
    url: '/dashboard/mapData',
    method: 'get'
  })
}

// 获取学段受助金额统计
export function getAmountBySchoolLevel() {
  return request({
    url: '/dashboard/amountBySchoolLevel',
    method: 'get'
  })
}

// 获取资助项目列表
export function getProjectList() {
  return request({
    url: '/dashboard/projectList',
    method: 'get'
  })
}

// 获取用户注册统计
export function getUserRegistrations() {
  return request({
    url: '/dashboard/userRegistrations',
    method: 'get'
  })
}

// 获取本学期受助人数趋势
export function getSubsidyTrend() {
  return request({
    url: '/dashboard/subsidyTrend',
    method: 'get'
  })
}
