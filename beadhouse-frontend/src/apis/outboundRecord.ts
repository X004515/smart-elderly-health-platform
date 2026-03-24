import { http } from '@/utils'

export interface IPageOutboundRecordByKey {
  pageNum?: number
  pageSize?: number
  warehouseName?: string
  materialName?: string
  recipient?: string
  outboundDate?: string[]
  startTime?: string
  endTime?: string
}

export interface IAddOutboundMaterialItem {
  warehouseMaterialId: number | string
  outboundNum: number
}

export interface IAddOutboundRecord {
  recipientType: string
  recipientId: number | string
  warehouseId: number | string
  outboundDate: string
  materialUse: string
  staffId: number | string
  outboundMaterialQueryList: IAddOutboundMaterialItem[]
}

export interface IAuditOutboundRecord {
  outboundRecordId: number | string
  auditResult: string
}

export interface IGetOutboundRecordById {
  outboundRecordId: number | string
}

export interface IPageWarehouseMaterialByKey {
  pageNum?: number
  pageSize?: number
  warehouseId: number | string
  materialName?: string
}

// 分页查询出库记录
export function pageOutboundRecordByKey(data: IPageOutboundRecordByKey) {
  const params = { ...data } as IPageOutboundRecordByKey
  if (params.outboundDate && params.outboundDate.length === 2) {
    params.startTime = params.outboundDate[0]
    params.endTime = params.outboundDate[1]
  }
  delete (params as any).outboundDate
  return http.get('/api/outboundRecord/pageOutboundRecordByKey', { params })
}

// 分页搜索老人
export function pageSearchElderByKey(data: { pageNum?: number; pageSize?: number; name?: string; phone?: string }) {
  return http.get('/api/outboundRecord/pageSearchElderByKey', { params: { ...data } })
}

// 分页搜索员工
export function pageSearchStaffByKey(data: { pageNum?: number; pageSize?: number; name?: string; phone?: string }) {
  return http.get('/api/outboundRecord/pageSearchStaffByKey', { params: { ...data } })
}

// 仓库列表
export function listWarehouse() {
  return http.get('/api/outboundRecord/listWarehouse')
}

// 登记人列表
export function listWarehouseStaff() {
  return http.get('/api/outboundRecord/listWarehouseStaff')
}

// 分页查询仓库物资
export function pageWarehouseMaterialByKey(data: IPageWarehouseMaterialByKey) {
  return http.get('/api/outboundRecord/pageWarehouseMaterialByKey', { params: { ...data } })
}

// 新增出库记录
export function addOutboundRecord(data: IAddOutboundRecord) {
  return http.post('/api/outboundRecord/addOutboundRecord', data)
}

// 根据编号查询出库记录
export function getOutboundRecordById(data: IGetOutboundRecordById) {
  return http.get('/api/outboundRecord/getOutboundRecordById', { params: { ...data } })
}

// 审核出库记录
export function auditOutboundRecord(data: IAuditOutboundRecord) {
  return http.put('/api/outboundRecord/auditOutboundRecord', data)
}

// 删除出库记录
export function deleteOutboundRecord(data: IGetOutboundRecordById) {
  return http.delete('/api/outboundRecord/deleteOutboundRecord', { params: { ...data } })
}
