import request from '@/utils/request'

// 查询文书生成记录列表（统一文书中心）
export function listDocRecord(query) {
  return request({
    url: '/system/docRecord/list',
    method: 'get',
    params: query
  })
}


