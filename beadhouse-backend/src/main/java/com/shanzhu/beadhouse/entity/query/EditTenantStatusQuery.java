package com.shanzhu.beadhouse.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "修改租户状态请求实体")
public class EditTenantStatusQuery {
    @ApiModelProperty(value = "租户id", required = true, example = "1")
    private Long tenantId;
    @ApiModelProperty(value = "启用状态（Y/N）", required = true, example = "Y")
    private String activeFlag;
}
