import request from '@/utils/request'

// 查询材料类型列表
export function listMaterialTypes(query) {
  return request({
    url: '/system/archiveMaterialConfig/materialTypes',
    method: 'get',
    params: query
  })
}

// 查询材料类型详细
export function getMaterialType(id) {
  return request({
    url: '/system/archiveMaterialConfig/materialType/' + id,
    method: 'get'
  })
}

// 新增材料类型
export function addMaterialType(data) {
  return request({
    url: '/system/archiveMaterialConfig/materialType',
    method: 'post',
    data: data
  })
}

// 修改材料类型
export function updateMaterialType(data) {
  return request({
    url: '/system/archiveMaterialConfig/materialType',
    method: 'put',
    data: data
  })
}

// 删除材料类型
export function delMaterialType(id) {
  return request({
    url: '/system/archiveMaterialConfig/materialType/' + id,
    method: 'delete'
  })
}

// 查询阶段材料配置
export function getStageConfigs(stageCode) {
  return request({
    url: '/system/archiveMaterialConfig/stageConfigs',
    method: 'get',
    params: { stageCode }
  })
}

// 保存阶段材料配置
export function saveStageConfig(data) {
  return request({
    url: '/system/archiveMaterialConfig/stageConfig',
    method: 'post',
    data: data
  })
}

// 检查档案阶段的材料完成情况
export function checkMaterials(archiveId, stageCode) {
  return request({
    url: '/system/archiveMaterialConfig/checkMaterials',
    method: 'get',
    params: { archiveId, stageCode }
  })
}

// 根据材料类型获取推荐的业务模块列表（后端逻辑）
export function getRecommendedBusinessModules(materialType) {
  return request({
    url: '/system/archiveMaterialConfig/getRecommendedBusinessModules',
    method: 'get',
    params: { materialType }
  })
}

// 获取所有业务模块选项（后端逻辑）
export function getBusinessModuleOptions() {
  return request({
    url: '/system/archiveMaterialConfig/getBusinessModuleOptions',
    method: 'get'
  })
}

// 根据业务模块和检查方式获取SQL配置模板（后端逻辑）
export function getSqlConfigTemplate(businessModule, checkMethod) {
  return request({
    url: '/system/archiveMaterialConfig/getSqlConfigTemplate',
    method: 'get',
    params: { businessModule, checkMethod }
  })
}


