package com.shanzhu.beadhouse.entity.vo;

import com.shanzhu.beadhouse.entity.query.OperateTenantQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "操作租户响应实体")
public class OperateTenantVo extends OperateTenantQuery {
}
