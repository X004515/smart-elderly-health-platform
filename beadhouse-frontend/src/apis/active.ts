import { http } from '@/utils'

export interface IPageActiveByKey {
  pageNum?: number
  pageSize?: number
  name?: string
  typeName?: string
  typeId?: number | string
  activeDateRange?: string[]
  startTime?: string
  endTime?: string
}

export interface IOperateActive {
  id?: number | string
  typeId: number | string
  theme: string
  name: string
  content: string
  address: string
  organizer: string
  phone: string
  activeDate: string
  activePicture: string
  elderIdList: number[]
}

export interface IGetActiveById {
  activeId: number | string
}

// 活动类型下拉
export function getActiveType() {
  return http.get('/api/active/getActiveType')
}

// 分页查询活动
export function pageActiveByKey(data: IPageActiveByKey) {
  const params = { ...data } as IPageActiveByKey
  if (params.activeDateRange && params.activeDateRange.length === 2) {
    params.startTime = params.activeDateRange[0]
    params.endTime = params.activeDateRange[1]
  }
  if (Reflect.has(params, 'typeName')) {
    params.typeId = Number(params.typeName) || undefined
  }
  delete (params as any).activeDateRange
  return http.get('/api/active/pageActiveByKey', { params })
}

// 分页搜索老人
export function pageSearchElderByKey(data: { pageNum?: number; pageSize?: number; name?: string; phone?: string }) {
  return http.get('/api/active/pageSearchElderByKey', { params: { ...data } })
}

// 新增活动
export function addActive(data: IOperateActive) {
  return http.post('/api/active/addActive', data)
}

// 根据编号查询活动
export function getActiveById(data: IGetActiveById) {
  return http.get('/api/active/getActiveById', { params: { ...data } })
}

// 编辑活动
export function editActive(data: IOperateActive) {
  return http.put('/api/active/editActive', data)
}

// 删除活动
export function deleteActive(data: IGetActiveById) {
  return http.delete('/api/active/deleteActive', { params: { ...data } })
}
