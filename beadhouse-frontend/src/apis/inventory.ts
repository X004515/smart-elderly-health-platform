import { http } from '@/utils'

export interface IPageInventoryByKey {
  pageNum?: number
  pageSize?: number
  warehouseId?: number | string
  materialName?: string
}

// 仓库列表
export function listWarehouse() {
  return http.get('/api/inventory/listWarehouse')
}

// 分页查询库存
export function pageInventoryByKey(data: IPageInventoryByKey) {
  return http.get('/api/inventory/pageInventoryByKey', { params: { ...data } })
}
