import { http } from "@/utils";

export interface IPageWarehouseRecordByKey {
  pageNum?: number;
  pageSize?: number;
  warehouseName?: string;
  materialName?: string;
  staffName?: string;
  warehouseDate?: string[];
  startTime?: string;
  endTime?: string;
}

export interface IAddWarehouseMaterialItem {
  materialId: number | string;
  warehouseNum: number;
  productDate: string;
  expireDate: string;
}

export interface IAddWarehouseRecord {
  warehouseId: number | string;
  staffId: number | string;
  source: string;
  warehouseDate: string;
  warehouseMaterialQueryList: IAddWarehouseMaterialItem[];
}

export interface IAuditWarehouseRecord {
  warehouseRecordId: number | string;
  auditResult: string;
}

export interface IGetWarehouseRecordById {
  warehouseRecordId: number | string;
}

// 分页查询入库记录
export function pageWarehouseRecordByKey(data: IPageWarehouseRecordByKey) {
  const params = { ...data } as IPageWarehouseRecordByKey;
  if (params.warehouseDate && params.warehouseDate.length === 2) {
    params.startTime = params.warehouseDate[0];
    params.endTime = params.warehouseDate[1];
  }
  delete (params as any).warehouseDate;
  return http.get("/api/warehouseRecord/pageWarehouseRecordByKey", { params });
}

// 仓库下拉
export function listWarehouse() {
  return http.get("/api/warehouseRecord/listWarehouse");
}

// 经办人下拉
export function listWarehouseStaff() {
  return http.get("/api/warehouseRecord/listWarehouseStaff");
}

// 物资分页查询（用于选择）
export function pageMaterialByKey(data: { pageNum?: number; pageSize?: number; materialName?: string }) {
  return http.get("/api/warehouseRecord/pageMaterialByKey", { params: { ...data } });
}

// 新增入库记录
export function addWarehouseRecord(data: IAddWarehouseRecord) {
  return http.post("/api/warehouseRecord/addWarehouseRecord", data);
}

// 根据编号查询入库记录
export function getWarehouseRecordById(data: IGetWarehouseRecordById) {
  return http.get("/api/warehouseRecord/getWarehouseRecordById", { params: { ...data } });
}

// 审核入库记录
export function auditWarehouseRecord(data: IAuditWarehouseRecord) {
  return http.put("/api/warehouseRecord/auditWarehouseRecord", data);
}

// 删除入库记录
export function deleteWarehouseRecord(data: IGetWarehouseRecordById) {
  return http.delete("/api/warehouseRecord/deleteWarehouseRecord", { params: { ...data } });
}

