import request from '@/utils/request'

// 查询待办任务列表
export function listTodoTasks() {
  return request({
    url: '/system/flowable/task/todoList',
    method: 'get'
  })
}

// 审批任务（通过或驳回）
export function approveTask(taskId, approved, comment) {
  return request({
    url: '/system/flowable/task/approve',
    method: 'post',
    params: {
      taskId,
      approved,
      comment
    }
  })
}

// 查询任务详情
export function getTaskDetail(taskId) {
  return request({
    url: '/system/flowable/task/detail',
    method: 'get',
    params: { taskId }
  })
}

// 查询学期档案流程的待办任务列表
export function listArchiveTodoTasks() {
  return request({
    url: '/system/flowable/task/archiveTodoList',
    method: 'get'
  })
}

// 完成学期档案阶段任务
export function completeArchiveTask(taskId, nextStage) {
  return request({
    url: '/system/flowable/task/completeArchiveTask',
    method: 'post',
    params: {
      taskId,
      nextStage
    }
  })
}

// 根据档案ID查询该档案的待办任务
export function getArchiveTodoTaskByArchiveId(archiveId) {
  return request({
    url: '/system/flowable/task/archiveTodoList/' + archiveId,
    method: 'get'
  })
}

