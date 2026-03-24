import { http } from "@/utils";

export interface IPageWarehouseByKey {
  pageNum?: number;
  pageSize?: number;
  name?: string;
  warehouseName?: string;
}

export interface IOperateWarehouse {
  id?: number | string;
  staffId: number | string;
  name: string;
}

export interface IGetWarehouseById {
  warehouseId: number | string;
}

// 分页查询仓库
export function pageWarehouseByKey(data: IPageWarehouseByKey) {
  const params = { ...data } as IPageWarehouseByKey;
  if (Reflect.has(params, "name")) {
    params.warehouseName = params.name;
  }
  return http.get("/api/warehouse/pageWarehouseByKey", { params });
}

// 仓库管理员下拉
export function listWarehouseStaff() {
  return http.get("/api/warehouse/listWarehouseStaff");
}

// 新增仓库
export function addWarehouse(data: IOperateWarehouse) {
  return http.post("/api/warehouse/addWarehouse", data);
}

// 根据编号查询仓库
export function getWarehouseById(data: IGetWarehouseById) {
  return http.get("/api/warehouse/getWarehouseById", { params: { ...data } });
}

// 编辑仓库
export function editWarehouse(data: IOperateWarehouse) {
  return http.put("/api/warehouse/editWarehouse", data);
}

// 删除仓库
export function deleteWarehouse(data: IGetWarehouseById) {
  return http.delete("/api/warehouse/deleteWarehouse", { params: { ...data } });
}

