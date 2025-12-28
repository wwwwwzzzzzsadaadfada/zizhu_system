import request from '@/utils/request'

// 查询自定义档案包列表
export function listPackageCustom(query) {
  return request({
    url: '/system/report/packageCustom/list',
    method: 'get',
    params: query
  })
}

// 查询自定义档案包详细
export function getPackageCustom(id) {
  return request({
    url: '/system/report/packageCustom/' + id,
    method: 'get'
  })
}

// 根据编码查询自定义档案包
export function getPackageCustomByCode(packageCode) {
  return request({
    url: '/system/report/packageCustom/code/' + packageCode,
    method: 'get'
  })
}

// 创建自定义档案包
export function createCustomPackage(data) {
  return request({
    url: '/system/report/packageCustom/create',
    method: 'post',
    data: data
  })
}

// 新增自定义档案包
export function addPackageCustom(data) {
  return request({
    url: '/system/report/packageCustom',
    method: 'post',
    data: data
  })
}

// 修改自定义档案包
export function updatePackageCustom(data) {
  return request({
    url: '/system/report/packageCustom',
    method: 'put',
    data: data
  })
}

// 删除自定义档案包
export function delPackageCustom(ids) {
  return request({
    url: '/system/report/packageCustom/' + ids,
    method: 'delete'
  })
}

// 更新档案包统计信息
export function updatePackageStatistics(packageCode) {
  return request({
    url: '/system/report/packageCustom/updateStatistics/' + packageCode,
    method: 'put'
  })
}
