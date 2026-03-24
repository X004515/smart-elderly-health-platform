import { http } from "@/utils";

export interface IPageMaterialByKey {
  pageNum?: number;
  pageSize?: number;
  name?: string;
  materialTypeName?: string;
  materialTypeId?: number;
  materialName?: string;
}

export interface IOperateMaterialType {
  id?: number | string;
  name: string;
}

export interface IGetMaterialTypeById {
  materialTypeId: number | string;
}

export interface IOperateMaterial {
  id?: number | string;
  typeId: number | string;
  name: string;
  price: number | string;
}

export interface IGetMaterialById {
  materialId: number | string;
}

// 物资分类下拉
export function getMaterialType(data: { materialTypeName?: string } = {}) {
  return http.get("/api/material/getMaterialType", { params: { ...data } });
}

// 分页查询物资
export function pageMaterialByKey(data: IPageMaterialByKey) {
  const params = { ...data } as IPageMaterialByKey;
  if (Reflect.has(params, "materialTypeName")) {
    params.materialTypeId = Number((params as any).materialTypeName) || undefined;
  }
  if (Reflect.has(params, "name")) {
    params.materialName = params.name;
  }
  return http.get("/api/material/pageMaterialByKey", { params });
}

// 新增物资分类
export function addMaterialType(data: IOperateMaterialType) {
  return http.post("/api/material/addMaterialType", data);
}

// 根据编号查询物资分类
export function getMaterialTypeById(data: IGetMaterialTypeById) {
  return http.get("/api/material/getMaterialTypeById", { params: { ...data } });
}

// 编辑物资分类
export function editMaterialType(data: IOperateMaterialType) {
  return http.put("/api/material/editMaterialType", data);
}

// 删除物资分类
export function deleteMaterialType(data: IGetMaterialTypeById) {
  return http.delete("/api/material/deleteMaterialType", { params: { ...data } });
}

// 新增物资
export function addMaterial(data: IOperateMaterial) {
  return http.post("/api/material/addMaterial", data);
}

// 根据编号查询物资
export function getMaterialById(data: IGetMaterialById) {
  return http.get("/api/material/getMaterialById", { params: { ...data } });
}

// 编辑物资
export function editMaterial(data: IOperateMaterial) {
  return http.put("/api/material/editMaterial", data);
}

// 删除物资
export function deleteMaterial(data: IGetMaterialById) {
  return http.delete("/api/material/deleteMaterial", { params: { ...data } });
}

