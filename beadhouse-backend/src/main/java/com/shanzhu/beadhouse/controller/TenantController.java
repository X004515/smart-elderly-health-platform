package com.shanzhu.beadhouse.controller;

import com.shanzhu.beadhouse.common.constant.Constant;
import com.shanzhu.beadhouse.entity.base.Result;
import com.shanzhu.beadhouse.entity.query.EditTenantStatusQuery;
import com.shanzhu.beadhouse.entity.query.OperateTenantQuery;
import com.shanzhu.beadhouse.entity.query.PageTenantByKeyQuery;
import com.shanzhu.beadhouse.entity.query.ResetTenantAdminPassQuery;
import com.shanzhu.beadhouse.service.TenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "租户管理")
@RestController
@RequestMapping("/tenant")
@PreAuthorize("@AuthorityAssert.hasAuthority('/platform/tenant')")
public class TenantController {
    @Resource
    private TenantService tenantService;

    @GetMapping("/pageTenantByKey")
    @ApiOperation(value = "分页查询租户", notes = Constant.DEVELOPER + Constant.EMPEROR_WEN)
    public Result pageTenantByKey(@ApiParam(value = "分页查询租户请求实体", required = true) PageTenantByKeyQuery query,
                                  @ApiParam(value = "接口访问请求头", required = true) @RequestHeader String token) {
        return tenantService.pageTenantByKey(query);
    }

    @PostMapping("/addTenant")
    @ApiOperation(value = "新增租户", notes = Constant.DEVELOPER + Constant.EMPEROR_WEN)
    public Result addTenant(@ApiParam(value = "新增租户请求实体", required = true) @RequestBody OperateTenantQuery query,
                            @ApiParam(value = "接口访问请求头", required = true) @RequestHeader String token) {
        return tenantService.addTenant(query);
    }

    @GetMapping("/getTenantById")
    @ApiOperation(value = "根据编号获取租户", notes = Constant.DEVELOPER + Constant.EMPEROR_WEN)
    public Result getTenantById(@ApiParam(value = "租户编号", required = true) @RequestParam Long tenantId,
                                @ApiParam(value = "接口访问请求头", required = true) @RequestHeader String token) {
        return tenantService.getTenantById(tenantId);
    }

    @PutMapping("/editTenant")
    @ApiOperation(value = "编辑租户", notes = Constant.DEVELOPER + Constant.EMPEROR_WEN)
    public Result editTenant(@ApiParam(value = "编辑租户请求实体", required = true) @RequestBody OperateTenantQuery query,
                             @ApiParam(value = "接口访问请求头", required = true) @RequestHeader String token) {
        return tenantService.editTenant(query);
    }

    @PutMapping("/editTenantStatus")
    @ApiOperation(value = "修改租户状态", notes = Constant.DEVELOPER + Constant.EMPEROR_WEN)
    public Result editTenantStatus(@ApiParam(value = "修改租户状态请求实体", required = true) @RequestBody EditTenantStatusQuery query,
                                   @ApiParam(value = "接口访问请求头", required = true) @RequestHeader String token) {
        return tenantService.editTenantStatus(query);
    }

    @PutMapping("/resetTenantAdminPass")
    @ApiOperation(value = "重置租户管理员密码", notes = Constant.DEVELOPER + Constant.EMPEROR_WEN)
    public Result resetTenantAdminPass(@ApiParam(value = "重置租户管理员密码请求实体", required = true) @RequestBody ResetTenantAdminPassQuery query,
                                       @ApiParam(value = "接口访问请求头", required = true) @RequestHeader String token) {
        return tenantService.resetTenantAdminPass(query);
    }
}
