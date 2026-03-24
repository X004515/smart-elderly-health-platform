import { http } from "@/utils";

interface IOperateLabelType {
  id?: number | string;
  name: string;
}

interface IOperateLabel {
  id?: number | string;
  typeId: number | string;
  name: string;
  color: string;
}

interface IGetLabelTypeById {
  typeId: number | string;
}

interface IGetLabelById {
  labelId: number | string;
}

// 获取客户标签列表
export function listLabel() {
  return http.get("/api/label/listLabel");
}

// 新增标签分类
export function addLabelType(data: IOperateLabelType) {
  return http.post("/api/label/addLabelType", data);
}

// 根据编号获取标签分类
export function getLabelTypeById(data: IGetLabelTypeById) {
  return http.get("/api/label/getLabelTypeById", {
    params: { ...data }
  });
}

// 编辑标签分类
export function editLabelType(data: IOperateLabelType) {
  return http.put("/api/label/editLabelType", data);
}

// 删除标签分类
export function deleteLabelType(data: IGetLabelTypeById) {
  return http.delete("/api/label/deleteLabelType", {
    params: { ...data }
  });
}

// 新增标签
export function addLabel(data: IOperateLabel) {
  return http.post("/api/label/addLabel", data);
}

// 根据编号获取标签
export function getLabelById(data: IGetLabelById) {
  return http.get("/api/label/getLabelById", {
    params: { ...data }
  });
}

// 编辑标签
export function editLabel(data: IOperateLabel) {
  return http.put("/api/label/editLabel", data);
}

// 删除标签
export function deleteLabel(data: IGetLabelById) {
  return http.delete("/api/label/deleteLabel", {
    params: { ...data }
  });
}
