import request from '@/utils/request'

// ==================== 阶段定义管理 ====================

// 查询阶段定义列表
export function listStageDef(query) {
  return request({
    url: '/system/archiveStage/list',
    method: 'get',
    params: query
  })
}

// 查询阶段定义详细
export function getStageDef(id) {
  return request({
    url: '/system/archiveStage/' + id,
    method: 'get'
  })
}

// 新增阶段定义
export function addStageDef(data) {
  return request({
    url: '/system/archiveStage',
    method: 'post',
    data: data
  })
}

// 修改阶段定义
export function updateStageDef(data) {
  return request({
    url: '/system/archiveStage',
    method: 'put',
    data: data
  })
}

// 删除阶段定义
export function delStageDef(id) {
  return request({
    url: '/system/archiveStage/' + id,
    method: 'delete'
  })
}

// 获取所有启用的阶段（按顺序）
export function getEnabledStages() {
  return request({
    url: '/system/archiveStage/enabledStages',
    method: 'get'
  })
}

// 根据阶段编码获取阶段定义
export function getStageDefByCode(stageCode) {
  return request({
    url: '/system/archiveStage/byCode/' + stageCode,
    method: 'get'
  })
}

// 获取下一个阶段
export function getNextStage(currentStageCode) {
  return request({
    url: '/system/archiveStage/nextStage',
    method: 'get',
    params: { currentStageCode }
  })
}

// 判断阶段是否完成
export function isStageCompleted(archiveId, stageCode) {
  return request({
    url: '/system/archiveStage/isCompleted',
    method: 'get',
    params: { archiveId, stageCode }
  })
}

// 判断是否可以进入下一阶段
export function canEnterNextStage(archiveId, currentStageCode) {
  return request({
    url: '/system/archiveStage/canEnterNext',
    method: 'get',
    params: { archiveId, currentStageCode }
  })
}

// ==================== 阶段业务关联管理 ====================

// 查询阶段业务关联列表
export function listStageBusiness(query) {
  return request({
    url: '/system/archiveStage/businesses',
    method: 'get',
    params: query
  })
}

// 获取阶段关联的业务模块列表
export function getStageBusinesses(stageCode) {
  return request({
    url: '/system/archiveStage/businesses/byStage',
    method: 'get',
    params: { stageCode }
  })
}

// 根据业务模块编码查询所属阶段
export function getStagesByBusinessModule(businessModule) {
  return request({
    url: '/system/archiveStage/businesses/byModule',
    method: 'get',
    params: { businessModule }
  })
}

// 检查业务模块是否属于指定阶段
export function checkBusinessInStage(businessModule, stageCode) {
  return request({
    url: '/system/archiveStage/businesses/check',
    method: 'get',
    params: { businessModule, stageCode }
  })
}

// 查询阶段业务关联详细
export function getStageBusiness(id) {
  return request({
    url: '/system/archiveStage/business/' + id,
    method: 'get'
  })
}

// 新增阶段业务关联
export function addStageBusiness(data) {
  return request({
    url: '/system/archiveStage/business',
    method: 'post',
    data: data
  })
}

// 修改阶段业务关联
export function updateStageBusiness(data) {
  return request({
    url: '/system/archiveStage/business',
    method: 'put',
    data: data
  })
}

// 删除阶段业务关联
export function delStageBusiness(id) {
  return request({
    url: '/system/archiveStage/business/' + id,
    method: 'delete'
  })
}

// 保存阶段业务关联配置（批量）
export function saveStageBusinessConfig(data) {
  return request({
    url: '/system/archiveStage/business/config',
    method: 'post',
    data: data
  })
}

// 获取阶段业务模块选项列表
export function getStageBusinessModuleOptions() {
  return request({
    url: '/system/archiveStage/businessModuleOptions',
    method: 'get'
  })
}

