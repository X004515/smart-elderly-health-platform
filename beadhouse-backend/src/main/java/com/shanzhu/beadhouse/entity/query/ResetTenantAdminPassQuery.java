package com.shanzhu.beadhouse.entity.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "重置租户管理员密码请求实体")
public class ResetTenantAdminPassQuery {
    @ApiModelProperty(value = "租户id", required = true, example = "1")
    private Long tenantId;
    @ApiModelProperty(value = "新密码", required = false, example = "123456")
    private String newPass;
}
