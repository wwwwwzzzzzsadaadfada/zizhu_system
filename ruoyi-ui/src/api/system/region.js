import request from '@/utils/request'

// 获取地区树形数据（用于级联选择器）
export function getRegionTree() {
  return request({
    url: '/system/region/tree',
    method: 'get'
  })
}

// 根据地区代码获取地区名称
export function getRegionName(code) {
  return request({
    url: '/system/region/name/' + code,
    method: 'get'
  })
}

