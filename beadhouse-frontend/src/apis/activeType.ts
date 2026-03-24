import { http } from '@/utils'

export interface IPageActiveTypeByKey {
  pageNum?: number
  pageSize?: number
  name?: string
  activeTypeName?: string
}

export interface IOperateActiveType {
  id?: number | string
  name: string
}

export interface IGetActiveTypeById {
  activeTypeId: number | string
}

// 分页查询活动分类
export function pageActiveTypeByKey(data: IPageActiveTypeByKey) {
  const params = { ...data } as IPageActiveTypeByKey
  if (Reflect.has(params, 'name')) {
    params.activeTypeName = params.name
  }
  return http.get('/api/activeType/pageActiveTypeByKey', { params })
}

// 新增活动分类（后端用 requestParam）
export function addActiveType(activeTypeName: string) {
  return http.post('/api/activeType/addActiveType', null, { params: { activeTypeName } })
}

// 根据编号查询活动分类
export function getActiveTypeById(data: IGetActiveTypeById) {
  return http.get('/api/activeType/getActiveTypeById', { params: { ...data } })
}

// 编辑活动分类
export function editActiveType(data: IOperateActiveType) {
  return http.put('/api/activeType/editActiveType', data)
}

// 删除活动分类
export function deleteActiveType(data: IGetActiveTypeById) {
  return http.delete('/api/activeType/deleteActiveType', { params: { ...data } })
}
