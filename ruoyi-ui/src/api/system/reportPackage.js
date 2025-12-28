import request from '@/utils/request'

// 查询报表打包记录列表
export function listReportPackage(query) {
  return request({
    url: '/system/report/package/list',
    method: 'get',
    params: query
  })
}

// 查询报表打包记录详细
export function getReportPackage(id) {
  return request({
    url: '/system/report/package/' + id,
    method: 'get'
  })
}

// 预览打包信息（不实际打包）
export function previewPackage(data) {
  return request({
    url: '/system/report/package/preview',
    method: 'post',
    data: data
  })
}

// 手动选择文件打包
export function createPackage(data) {
  return request({
    url: '/system/report/package/create',
    method: 'post',
    data: data
  })
}

// 按条件打包全部档案
export function createPackageByCondition(data) {
  return request({
    url: '/system/report/package/createByCondition',
    method: 'post',
    data: data
  })
}

// 下载打包的zip文件
export function downloadPackage(id) {
  return request({
    url: '/system/report/package/download/' + id,
    method: 'get',
    responseType: 'blob'
  })
}

// 删除报表打包记录
export function delReportPackage(id) {
  return request({
    url: '/system/report/package/' + id,
    method: 'delete'
  })
}

// 批量删除报表打包记录
export function delReportPackages(ids) {
  return request({
    url: '/system/report/package/' + ids,
    method: 'delete'
  })
}

// 查询打包明细列表
export function getPackageItems(packageId) {
  return request({
    url: '/system/report/package/item/' + packageId,
    method: 'get'
  })
}

