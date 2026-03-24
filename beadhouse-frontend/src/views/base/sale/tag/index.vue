<template>
  <div class="tag-page">
    <div class="tool-bar">
      <el-button type="primary" :icon="Plus" plain @click="openTypeDialog('新增')">新增分类</el-button>
      <el-button type="primary" plain :icon="Plus" :disabled="!selectedTypeId" @click="openLabelDialog('新增')">
        新增标签
      </el-button>
    </div>

    <div class="content">
      <div class="panel">
        <div class="panel-title">标签分类</div>
        <el-table
          :data="typeList"
          border
          highlight-current-row
          @current-change="handleTypeCurrentChange"
          :current-row-key="selectedTypeId"
          row-key="id"
        >
          <el-table-column label="分类名称" prop="name" min-width="120" />
          <el-table-column label="标签数" width="80">
            <template #default="{ row }">
              {{ (row.labelItemList || []).length }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link @click="openTypeDialog('编辑', row)">编辑</el-button>
              <el-popconfirm title="确认删除该分类吗？" confirm-button-type="danger" @confirm="deleteType(row)">
                <template #reference>
                  <el-button size="small" link>删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="panel">
        <div class="panel-title">标签列表（{{ selectedTypeName || "请先选择分类" }}）</div>
        <el-table :data="currentLabelList" border>
          <el-table-column label="标签名称" prop="name" min-width="120" />
          <el-table-column label="颜色" width="120">
            <template #default="{ row }">
              <el-tag :style="{ backgroundColor: row.color, color: '#fff', borderColor: row.color }">{{ row.color }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link @click="openLabelDialog('编辑', row)">编辑</el-button>
              <el-popconfirm title="确认删除该标签吗？" confirm-button-type="danger" @confirm="deleteTag(row)">
                <template #reference>
                  <el-button size="small" link>删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <el-dialog v-model="typeDialogVisible" :title="typeDialogTitle" width="420px" destroy-on-close>
      <el-form :model="typeFormData" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="typeFormData.name" placeholder="请输入分类名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitType">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="labelDialogVisible" :title="labelDialogTitle" width="460px" destroy-on-close>
      <el-form :model="labelFormData" label-width="80px">
        <el-form-item label="分类">
          <el-input :model-value="selectedTypeName" disabled />
        </el-form-item>
        <el-form-item label="标签名称">
          <el-input v-model="labelFormData.name" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="标签颜色">
          <el-input v-model="labelFormData.color" placeholder="请输入颜色，如 #409EFF" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="labelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitLabel">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import {
  addLabel,
  addLabelType,
  deleteLabel,
  deleteLabelType,
  editLabel,
  editLabelType,
  listLabel
} from "@/apis/label";

const typeList = ref<any[]>([]);
const selectedTypeId = ref<number | string>("");
const selectedTypeName = ref("");

const typeDialogVisible = ref(false);
const typeDialogTitle = ref("新增");
const typeFormData = ref<any>({
  id: "",
  name: ""
});

const labelDialogVisible = ref(false);
const labelDialogTitle = ref("新增");
const labelFormData = ref<any>({
  id: "",
  typeId: "",
  name: "",
  color: "#409EFF"
});

const currentLabelList = computed(() => {
  const currentType = typeList.value.find(item => item.id === selectedTypeId.value);
  return currentType?.labelItemList || [];
});

const loadLabelList = async () => {
  const res: any = await listLabel();
  if (res.code !== 200) {
    ElMessage.error(res.msg || "获取标签列表失败");
    return;
  }
  const nextTypeList = res.data || [];
  typeList.value = nextTypeList;
  if (!nextTypeList.length) {
    selectedTypeId.value = "";
    selectedTypeName.value = "";
    return;
  }
  if (!selectedTypeId.value || !nextTypeList.some(item => item.id === selectedTypeId.value)) {
    selectedTypeId.value = nextTypeList[0].id;
    selectedTypeName.value = nextTypeList[0].name;
    return;
  }
  const currentType = nextTypeList.find(item => item.id === selectedTypeId.value);
  selectedTypeName.value = currentType?.name || "";
};

const handleTypeCurrentChange = (row: any) => {
  if (!row) {
    return;
  }
  selectedTypeId.value = row.id;
  selectedTypeName.value = row.name;
};

const openTypeDialog = (title: string, row: any = {}) => {
  typeDialogTitle.value = title;
  if (title === "编辑") {
    typeFormData.value = {
      id: row.id,
      name: row.name
    };
  } else {
    typeFormData.value = {
      id: "",
      name: ""
    };
  }
  typeDialogVisible.value = true;
};

const submitType = async () => {
  if (!typeFormData.value.name) {
    ElMessage.warning("请输入分类名称");
    return;
  }
  const req = typeDialogTitle.value === "新增" ? addLabelType : editLabelType;
  const res: any = await req(typeFormData.value);
  if (res.code === 200) {
    ElMessage.success(res.msg || "操作成功");
    typeDialogVisible.value = false;
    await loadLabelList();
  } else {
    ElMessage.error(res.msg || "操作失败");
  }
};

const deleteType = async (row: any) => {
  const res: any = await deleteLabelType({ typeId: row.id });
  if (res.code === 200) {
    ElMessage.success(res.msg || "删除成功");
    await loadLabelList();
  } else {
    ElMessage.error(res.msg || "删除失败");
  }
};

const openLabelDialog = (title: string, row: any = {}) => {
  if (!selectedTypeId.value) {
    ElMessage.warning("请先选择分类");
    return;
  }
  labelDialogTitle.value = title;
  if (title === "编辑") {
    labelFormData.value = {
      id: row.id,
      typeId: row.typeId || selectedTypeId.value,
      name: row.name,
      color: row.color || "#409EFF"
    };
  } else {
    labelFormData.value = {
      id: "",
      typeId: selectedTypeId.value,
      name: "",
      color: "#409EFF"
    };
  }
  labelDialogVisible.value = true;
};

const submitLabel = async () => {
  if (!labelFormData.value.name) {
    ElMessage.warning("请输入标签名称");
    return;
  }
  if (!labelFormData.value.color) {
    ElMessage.warning("请输入标签颜色");
    return;
  }
  const req = labelDialogTitle.value === "新增" ? addLabel : editLabel;
  const res: any = await req(labelFormData.value);
  if (res.code === 200) {
    ElMessage.success(res.msg || "操作成功");
    labelDialogVisible.value = false;
    await loadLabelList();
  } else {
    ElMessage.error(res.msg || "操作失败");
  }
};

const deleteTag = async (row: any) => {
  const res: any = await deleteLabel({ labelId: row.id });
  if (res.code === 200) {
    ElMessage.success(res.msg || "删除成功");
    await loadLabelList();
  } else {
    ElMessage.error(res.msg || "删除失败");
  }
};

onMounted(() => {
  loadLabelList();
});
</script>

<style lang="scss" scoped>
.tag-page {
  padding: 12px;
}

.tool-bar {
  margin-bottom: 12px;
  display: flex;
  gap: 10px;
}

.content {
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 12px;
}

.panel {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px;
  background-color: #fff;
}

.panel-title {
  margin-bottom: 10px;
  font-weight: 600;
}

@media (max-width: 980px) {
  .content {
    grid-template-columns: 1fr;
  }
}
</style>
