package com.shanzhu.beadhouse.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "分页查询租户请求实体")
public class PageTenantByKeyQuery {
    @ApiModelProperty(value = "页码", required = true, example = "1")
    private Integer pageNum;
    @ApiModelProperty(value = "条数", required = true, example = "10")
    private Integer pageSize;
    @ApiModelProperty(value = "租户编码", required = false, example = "default")
    private String tenantCode;
    @ApiModelProperty(value = "租户名称", required = false, example = "Default Tenant")
    private String tenantName;
    @ApiModelProperty(value = "启用状态（Y/N）", required = false, example = "Y")
    private String activeFlag;
}
