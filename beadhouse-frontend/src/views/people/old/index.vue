<template>
  <div class="table-box">
    <ProTable
      ref="proTable"
      title="用户列表"
      :columns="columns"
      :requestApi="getTableList"
      :initParam="initParam"
      :dataCallback="dataCallback"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader>
        <el-button class="bg-blue" @click="exportExcelData" type="primary">
          <IconPark :icon="Download" class="mr-1"></IconPark>
          <span>导出</span>
        </el-button>
      </template>

      <!-- 表格操作 -->
      <template #operation="scope">
        <el-button
          size="small"
          link
          :icon="View"
          @click="openDrawer('长者档案',scope)"
        >
          长者档案
        </el-button>
        <el-button
          size="small"
          link
          :icon="EditPen"
          @click="openDrawer('编辑',scope)"
        >
          编辑
        </el-button>
        <el-button
          size="small"
          link
          :icon="MagicStick"
          @click="openRecommendDialog(scope.row)"
        >
          活动推荐
        </el-button>
        <el-button
          size="small"
          link
          :icon="CollectionTag"
          @click="openLabelDialog(scope.row)"
        >
          兴趣标签
        </el-button>
        <el-popconfirm
          title="Are you sure to delete this?"
          @confirm="deleteData(scope)"
          confirm-button-type="danger"
        >
          <template #reference>
            <el-button size="small" link :icon="Delete">删除</el-button>
          </template>
        </el-popconfirm>
      </template>
    </ProTable>
    <oldDialog ref="DialogRef" />
    <el-dialog
      v-model="recommendDialogVisible"
      width="900px"
      title="活动推荐（兴趣+行为）"
      destroy-on-close
    >
      <div class="recommend-toolbar">
        <div>长者：{{ recommendElderName }}</div>
        <div class="recommend-toolbar-right">
          <span>推荐条数：</span>
          <el-input-number
            v-model="recommendTopN"
            :min="1"
            :max="20"
            size="small"
          />
          <el-button size="small" type="primary" @click="loadRecommendList">
            刷新推荐
          </el-button>
        </div>
      </div>
      <el-table
        :data="recommendList"
        border
        v-loading="recommendLoading"
        max-height="480"
      >
        <el-table-column prop="name" label="活动名称" min-width="140" />
        <el-table-column prop="typeName" label="活动分类" width="120" />
        <el-table-column prop="activeDate" label="活动日期" width="120" />
        <el-table-column label="推荐分" width="100">
          <template #default="{ row }">
            <el-tag type="success">{{ row.score }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="90">
          <template #default="{ row }">
            <el-tag
              :type="row.recommendLevel === '高匹配' ? 'danger' : row.recommendLevel === '中匹配' ? 'warning' : 'info'"
            >
              {{ row.recommendLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="推荐原因" min-width="320">
          <template #default="{ row }">
            {{ (row.reasonList || []).join("；") }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
    <el-dialog
      v-model="labelDialogVisible"
      width="700px"
      title="兴趣标签编辑"
      destroy-on-close
    >
      <div class="label-toolbar">
        <span>长者：{{ labelElderName }}</span>
      </div>
      <div v-loading="labelLoading">
        <template v-if="labelGroupList.length">
          <div
            class="label-group"
            v-for="group in labelGroupList"
            :key="group.name"
          >
            <div class="label-group-title">{{ group.name }}</div>
            <el-checkbox-group v-model="labelIdList">
              <el-checkbox
                v-for="item in group.labelTypeItemList || []"
                :key="item.id"
                :label="item.id"
              >
                {{ item.name }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </template>
        <el-empty v-else description="暂无可选标签" />
      </div>
      <template #footer>
        <el-button @click="labelDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="labelSaving" @click="submitLabelEdit">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="useProTable">
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import oldDialog from "./oldDialog/index.vue";
import { Download } from "@icon-park/vue-next";
import { ColumnProps } from "@/components/ProTable/interface";
import ProTable from "@/components/ProTable/index.vue";
import { CollectionTag, Delete, EditPen, MagicStick, View } from "@element-plus/icons-vue";
import {
  deleteElder,
  editElderLabel,
  editElder,
  exportExcel,
  getEditElderLabelById,
  pageElderByKey,
  recommendActiveByElder,
  sexList
} from "@/apis/elderRecord";

// 获取 ProTable 元素，调用其获取刷新数据方法（还能获取到当前查询参数，方便导出携带参数）
const proTable = ref();
const initParam = reactive({});

// dataCallback 是对于返回的表格数据做处理，如果你后台返回的数据不是 list && total && pageNum && pageSize 这些字段，那么你可以在这里进行处理成这些字段
// 或者直接去 hooks/useTable.ts 文件中把字段改为你后端对应的就行
const dataCallback = (data: any) => {
  return {
    list: data.list,
    total: data.total,
    pageNum: data.pageNum,
    pageSize: data.pageSize
  };
};

// 如果你想在请求之前对当前请求参数做一些操作，可以自定义如下函数：params 为当前所有的请求参数（包括分页），最后返回请求列表接口
// 默认不做操作就直接在 ProTable 组件上绑定	:requestApi="getUserList"
const getTableList = (params: any) => {
  let newParams = JSON.parse(JSON.stringify(params));
  return pageElderByKey(newParams);
};

// 导出Excel
const exportExcelData = async () => {
  const res:any = await exportExcel()
  const anchor = document.createElement("a"); // create the anchor element
  anchor.href = res.data;
  anchor.click()
  anchor.remove()
  responseHint(res)
};

// 删除
const deleteData = async (params: any) => {
  const res: any = await deleteElder({
    elderId: params.row.id
  });
  responseHint(res);
};

// 响应提示
const responseHint = (res: any) => {
  if (res.code === 200) {
    ElMessage.success(res.msg);
    proTable.value.getTableList();
  } else {
    ElMessage.error(res.msg);
  }
};

// 打开 drawer(新增、查看、编辑)
const DialogRef = ref();
const openDrawer = (title: string, rowData: any = {}) => {
  const params = {
    title,
    rowData: { ...rowData.row },
    isView: title === "长者档案",
    api: title === "编辑" ? editElder : "",
    getTableList: proTable.value.getTableList
  };
  DialogRef.value.acceptParams(params);
};

const recommendDialogVisible = ref(false);
const recommendLoading = ref(false);
const recommendList = ref<any[]>([]);
const recommendTopN = ref(5);
const recommendElderId = ref<number | string>("");
const recommendElderName = ref("");

const openRecommendDialog = async (row: any) => {
  recommendDialogVisible.value = true;
  recommendElderId.value = row.id;
  recommendElderName.value = row.name || "-";
  recommendTopN.value = 5;
  await loadRecommendList();
};

const loadRecommendList = async () => {
  if (!recommendElderId.value) {
    return;
  }
  recommendLoading.value = true;
  try {
    const res: any = await recommendActiveByElder({
      elderId: recommendElderId.value,
      topN: recommendTopN.value
    });
    if (res.code === 200) {
      recommendList.value = res.data || [];
    } else {
      recommendList.value = [];
      ElMessage.error(res.msg || "推荐失败");
    }
  } finally {
    recommendLoading.value = false;
  }
};

const labelDialogVisible = ref(false);
const labelLoading = ref(false);
const labelSaving = ref(false);
const labelElderId = ref<number | string>("");
const labelElderName = ref("");
const labelGroupList = ref<any[]>([]);
const labelIdList = ref<Array<number | string>>([]);

const openLabelDialog = async (row: any) => {
  labelDialogVisible.value = true;
  labelElderId.value = row.id;
  labelElderName.value = row.name || "-";
  await loadLabelData();
};

const loadLabelData = async () => {
  if (!labelElderId.value) {
    return;
  }
  labelLoading.value = true;
  try {
    const res: any = await getEditElderLabelById({
      elderId: labelElderId.value
    });
    if (res.code === 200) {
      const groupList = res.data || [];
      labelGroupList.value = groupList;
      const selectedList: Array<number | string> = [];
      groupList.forEach((group: any) => {
        (group.labelTypeItemList || []).forEach((item: any) => {
          if (item.isCheck) {
            selectedList.push(item.id);
          }
        });
      });
      labelIdList.value = selectedList;
    } else {
      labelGroupList.value = [];
      labelIdList.value = [];
      ElMessage.error(res.msg || "获取标签失败");
    }
  } finally {
    labelLoading.value = false;
  }
};

const submitLabelEdit = async () => {
  if (!labelElderId.value) {
    return;
  }
  labelSaving.value = true;
  try {
    const res: any = await editElderLabel({
      elderId: labelElderId.value,
      labelIdList: labelIdList.value
    });
    if (res.code === 200) {
      ElMessage.success(res.msg || "标签更新成功");
      labelDialogVisible.value = false;
    } else {
      ElMessage.error(res.msg || "标签更新失败");
    }
  } finally {
    labelSaving.value = false;
  }
};

// 表格配置项
const columns: ColumnProps<any>[] = [
  { prop: "rank", label: "序号", width: 55 },
  { prop: "name", label: "老人姓名", width: 100, search: { el: "input" } },
  { prop: "bedName", label: "床位名称" },
  {
    enum: sexList,
    prop: "sex",
    label: "老人性别",
    width: 100,
    search: { el: "select", props: { filterable: true } }
  },
  { prop: "age", label: "老人年龄", width: 100 },
  { prop: "idNum", label: "身份证号", search: { el: "input" } },
  { prop: "phone", label: "老人电话" },
  { prop: "checkFlag", label: "入住状态" },
  { prop: "operation", label: "操作", width: 360 }
];
</script>

<style lang="scss" scoped>
.recommend-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.recommend-toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.label-toolbar {
  margin-bottom: 12px;
}

.label-group {
  padding: 10px 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  margin-bottom: 12px;
}

.label-group-title {
  margin-bottom: 8px;
  font-weight: 600;
}
</style>
