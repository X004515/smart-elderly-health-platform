package com.shanzhu.beadhouse.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "操作租户请求实体")
public class OperateTenantQuery {
    @ApiModelProperty(value = "id", required = false, example = "1")
    private Long id;
    @ApiModelProperty(value = "租户编码", required = true, example = "default")
    private String tenantCode;
    @ApiModelProperty(value = "租户名称", required = true, example = "Default Tenant")
    private String tenantName;
    @ApiModelProperty(value = "启用状态（Y/N）", required = false, example = "Y")
    private String activeFlag;
    @ApiModelProperty(value = "到期时间", required = false, example = "2026-12-31 23:59:59")
    private Date expireTime;
    @ApiModelProperty(value = "联系人", required = false, example = "张三")
    private String contactName;
    @ApiModelProperty(value = "联系电话", required = false, example = "13500000000")
    private String contactPhone;
    @ApiModelProperty(value = "备注", required = false, example = "毕业版演示租户")
    private String remark;
    @ApiModelProperty(value = "租户管理员姓名（新增必填）", required = false, example = "租户管理员")
    private String adminName;
    @ApiModelProperty(value = "租户管理员手机号（新增必填）", required = false, example = "13600000000")
    private String adminPhone;
    @ApiModelProperty(value = "租户管理员邮箱（新增可选）", required = false, example = "tenant-admin@example.com")
    private String adminEmail;
}
