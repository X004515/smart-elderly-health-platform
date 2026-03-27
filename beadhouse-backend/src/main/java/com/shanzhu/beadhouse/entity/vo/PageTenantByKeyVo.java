package com.shanzhu.beadhouse.entity.vo;

import com.shanzhu.beadhouse.entity.base.Rank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "分页查询租户响应实体")
public class PageTenantByKeyVo extends Rank {
    @ApiModelProperty(value = "id", example = "1")
    private Long id;
    @ApiModelProperty(value = "租户编码", example = "default")
    private String tenantCode;
    @ApiModelProperty(value = "租户名称", example = "Default Tenant")
    private String tenantName;
    @ApiModelProperty(value = "启用状态（Y/N）", example = "Y")
    private String activeFlag;
    @ApiModelProperty(value = "到期时间", example = "2026-12-31 23:59:59")
    private Date expireTime;
    @ApiModelProperty(value = "联系人", example = "张三")
    private String contactName;
    @ApiModelProperty(value = "联系电话", example = "13500000000")
    private String contactPhone;
    @ApiModelProperty(value = "备注", example = "毕业版演示租户")
    private String remark;
}
