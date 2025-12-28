import request from '@/utils/request'

// 查询学期资助档案列表
export function listAidArchive(query) {
  return request({
    url: '/system/aidArchive/list',
    method: 'get',
    params: query
  })
}

// 查询学期资助档案详细
export function getAidArchive(id) {
  return request({
    url: '/system/aidArchive/' + id,
    method: 'get'
  })
}

// 新增学期资助档案
export function addAidArchive(data) {
  return request({
    url: '/system/aidArchive',
    method: 'post',
    data: data
  })
}

// 修改学期资助档案
export function updateAidArchive(data) {
  return request({
    url: '/system/aidArchive',
    method: 'put',
    data: data
  })
}

// 删除学期资助档案
export function delAidArchive(id) {
  return request({
    url: '/system/aidArchive/' + id,
    method: 'delete'
  })
}

// 查询档案下的文书记录列表
export function listArchiveDocs(archiveId) {
  return request({
    url: '/system/aidArchive/' + archiveId + '/docs',
    method: 'get'
  })
}

// 检查档案是否满足从"申请与材料"阶段切换到"审核与金额"阶段的条件（例如是否有文书记录）
export function checkApplyToAuditReady(id) {
  return request({
    url: '/system/aidArchive/' + id + '/checkApplyToAuditReady',
    method: 'get'
  })
}

// 检查档案是否满足归档条件（例如是否有已通过的受助记录）
export function checkArchiveReady(id) {
  return request({
    url: '/system/aidArchive/' + id + '/checkArchiveReady',
    method: 'get'
  })
}

// 查询学期档案的流程历史记录
export function getArchiveProcessHistory(id) {
  return request({
    url: '/system/aidArchive/' + id + '/processHistory',
    method: 'get'
  })
}

// 批量生成档案
export function batchCreateArchives(data) {
  return request({
    url: '/system/aidArchive/batch',
    method: 'post',
    data: data
  })
}

// ==================== 6阶段流程支持接口 ====================

// 获取所有启用的阶段列表（按顺序）
export function getEnabledStages() {
  return request({
    url: '/system/aidArchive/stages',
    method: 'get'
  })
}

// 检查档案所有阶段的完成情况
export function getAllStagesStatus(archiveId) {
  return request({
    url: '/system/aidArchive/' + archiveId + '/allStagesStatus',
    method: 'get'
  })
}

// 切换到下一阶段
export function switchToNextStage(archiveId) {
  return request({
    url: '/system/aidArchive/' + archiveId + '/nextStage',
    method: 'post'
  })
}

// 检查是否可以进入下一阶段
export function canEnterNextStage(archiveId) {
  return request({
    url: '/system/aidArchive/' + archiveId + '/canEnterNextStage',
    method: 'get'
  })
}


