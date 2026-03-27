package com.shanzhu.beadhouse.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shanzhu.beadhouse.common.config.tenant.SaasTenantConfig;
import com.shanzhu.beadhouse.common.constant.Constant;
import com.shanzhu.beadhouse.common.constant.ExceptionEnum;
import com.shanzhu.beadhouse.common.constant.YesNoEnum;
import com.shanzhu.beadhouse.common.util.AesUtil;
import com.shanzhu.beadhouse.common.util.AssertUtil;
import com.shanzhu.beadhouse.common.util.PageUtil;
import com.shanzhu.beadhouse.dao.mapper.AuthMapper;
import com.shanzhu.beadhouse.dao.mapper.RoleAuthMapper;
import com.shanzhu.beadhouse.dao.mapper.RoleMapper;
import com.shanzhu.beadhouse.dao.mapper.StaffMapper;
import com.shanzhu.beadhouse.dao.mapper.TenantMapper;
import com.shanzhu.beadhouse.entity.base.PageResult;
import com.shanzhu.beadhouse.entity.base.Result;
import com.shanzhu.beadhouse.entity.po.Auth;
import com.shanzhu.beadhouse.entity.po.Role;
import com.shanzhu.beadhouse.entity.po.RoleAuth;
import com.shanzhu.beadhouse.entity.po.Staff;
import com.shanzhu.beadhouse.entity.po.Tenant;
import com.shanzhu.beadhouse.entity.query.EditTenantStatusQuery;
import com.shanzhu.beadhouse.entity.query.OperateTenantQuery;
import com.shanzhu.beadhouse.entity.query.PageTenantByKeyQuery;
import com.shanzhu.beadhouse.entity.query.ResetTenantAdminPassQuery;
import com.shanzhu.beadhouse.entity.vo.OperateTenantVo;
import com.shanzhu.beadhouse.entity.vo.PageTenantByKeyVo;
import com.shanzhu.beadhouse.service.TenantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TenantServiceImpl implements TenantService {
    private static final String TENANT_ADMIN_ROLE_NAME = "租户管理员";
    private static final String PLATFORM_ADMIN_ROLE_NAME = "平台管理员";

    @Resource
    private TenantMapper tenantMapper;
    @Resource
    private StaffMapper staffMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoleAuthMapper roleAuthMapper;
    @Resource
    private AuthMapper authMapper;
    @Resource
    private PageUtil pageUtil;
    @Resource
    private SaasTenantConfig saasTenantConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result pageTenantByKey(PageTenantByKeyQuery query) {
        ensurePlatformAdminAccountMigrated();
        QueryWrapper<Tenant> wrapper = new QueryWrapper<>();
        if (ObjUtil.isNotEmpty(query.getTenantCode())) {
            wrapper.lambda().like(Tenant::getTenantCode, query.getTenantCode());
        }
        if (ObjUtil.isNotEmpty(query.getTenantName())) {
            wrapper.lambda().like(Tenant::getTenantName, query.getTenantName());
        }
        if (ObjUtil.isNotEmpty(query.getActiveFlag())) {
            wrapper.lambda().eq(Tenant::getActiveFlag, query.getActiveFlag());
        }
        wrapper.lambda().orderByAsc(Tenant::getId);
        List<Tenant> tenantList = tenantMapper.selectList(wrapper);
        List<PageTenantByKeyVo> voList = BeanUtil.copyToList(tenantList, PageTenantByKeyVo.class);
        Integer pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        Integer pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        PageResult<PageTenantByKeyVo> pageResult = pageUtil.packPageResultData(voList, pageNum, pageSize);
        return Result.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addTenant(OperateTenantQuery query) {
        ensurePlatformAdminAccountMigrated();
        checkTenantUnique(query.getTenantCode(), query.getTenantName(), null);
        // 新增租户
        Tenant tenant = new Tenant();
        tenant.setTenantCode(query.getTenantCode());
        tenant.setTenantName(query.getTenantName());
        tenant.setActiveFlag(ObjUtil.isNotEmpty(query.getActiveFlag()) ? query.getActiveFlag() : YesNoEnum.YES.getCode());
        tenant.setExpireTime(query.getExpireTime());
        tenant.setContactName(query.getContactName());
        tenant.setContactPhone(query.getContactPhone());
        tenant.setRemark(query.getRemark());
        tenantMapper.insert(tenant);
        // 新增租户管理员角色并复制模板权限（排除平台权限）
        Role tenantAdminRole = new Role();
        tenantAdminRole.setTenantId(tenant.getId());
        tenantAdminRole.setName(TENANT_ADMIN_ROLE_NAME);
        roleMapper.insert(tenantAdminRole);
        copyRoleAuthByTemplate(tenantAdminRole.getId(), tenant.getId());
        // 新增租户管理员账号
        Staff tenantAdmin = new Staff();
        tenantAdmin.setTenantId(tenant.getId());
        tenantAdmin.setRoleId(tenantAdminRole.getId());
        tenantAdmin.setName(ObjUtil.isNotEmpty(query.getAdminName()) ? query.getAdminName() : TENANT_ADMIN_ROLE_NAME);
        tenantAdmin.setIdNum(genTenantAdminIdNum(tenant.getId()));
        tenantAdmin.setAge(30);
        tenantAdmin.setSex("男");
        tenantAdmin.setPhone(query.getAdminPhone());
        tenantAdmin.setEmail(ObjUtil.isNotEmpty(query.getAdminEmail()) ? query.getAdminEmail() : (query.getAdminPhone() + "@tenant.local"));
        tenantAdmin.setPass(AesUtil.aesEncode(saasTenantConfig.getTenantAdmin().getDefaultPass()));
        tenantAdmin.setAvator("");
        tenantAdmin.setAddress("");
        tenantAdmin.setLeaveFlag(YesNoEnum.NO.getCode());
        staffMapper.insert(tenantAdmin);
        return Result.success();
    }

    @Override
    public Result getTenantById(Long tenantId) {
        ensurePlatformAdminAccountMigrated();
        Tenant tenant = tenantMapper.selectById(tenantId);
        AssertUtil.notNull(tenant, ExceptionEnum.DATA_NOT_EXIST);
        OperateTenantVo operateTenantVo = BeanUtil.toBean(tenant, OperateTenantVo.class);
        Staff tenantAdmin = getTenantAdmin(tenantId);
        if (tenantAdmin != null) {
            operateTenantVo.setAdminName(tenantAdmin.getName());
            operateTenantVo.setAdminPhone(tenantAdmin.getPhone());
            operateTenantVo.setAdminEmail(tenantAdmin.getEmail());
        }
        return Result.success(operateTenantVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result editTenant(OperateTenantQuery query) {
        ensurePlatformAdminAccountMigrated();
        Tenant tenant = tenantMapper.selectById(query.getId());
        AssertUtil.notNull(tenant, ExceptionEnum.DATA_NOT_EXIST);
        checkTenantUnique(query.getTenantCode(), query.getTenantName(), query.getId());
        Tenant update = new Tenant();
        update.setId(query.getId());
        update.setTenantCode(query.getTenantCode());
        update.setTenantName(query.getTenantName());
        update.setActiveFlag(ObjUtil.isNotEmpty(query.getActiveFlag()) ? query.getActiveFlag() : tenant.getActiveFlag());
        update.setExpireTime(query.getExpireTime());
        update.setContactName(query.getContactName());
        update.setContactPhone(query.getContactPhone());
        update.setRemark(query.getRemark());
        tenantMapper.updateById(update);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result editTenantStatus(EditTenantStatusQuery query) {
        ensurePlatformAdminAccountMigrated();
        Tenant tenant = tenantMapper.selectById(query.getTenantId());
        AssertUtil.notNull(tenant, ExceptionEnum.DATA_NOT_EXIST);
        Tenant update = new Tenant();
        update.setId(query.getTenantId());
        update.setActiveFlag(query.getActiveFlag());
        tenantMapper.updateById(update);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result resetTenantAdminPass(ResetTenantAdminPassQuery query) {
        ensurePlatformAdminAccountMigrated();
        Staff tenantAdmin = getTenantAdmin(query.getTenantId());
        AssertUtil.notNull(tenantAdmin, ExceptionEnum.DATA_NOT_EXIST);
        tenantAdmin.setPass(AesUtil.aesEncode(ObjUtil.isNotEmpty(query.getNewPass()) ? query.getNewPass() : saasTenantConfig.getTenantAdmin().getDefaultPass()));
        staffMapper.updateById(tenantAdmin);
        return Result.success();
    }

    private void checkTenantUnique(String tenantCode, String tenantName, Long selfId) {
        Tenant byCode = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getTenantCode, tenantCode));
        boolean checkCode = byCode != null && !Objects.equals(byCode.getId(), selfId);
        AssertUtil.notTrue(checkCode, ExceptionEnum.TENANT_CODE_REPEAT);
        Tenant byName = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getTenantName, tenantName));
        boolean checkName = byName != null && !Objects.equals(byName.getId(), selfId);
        AssertUtil.notTrue(checkName, ExceptionEnum.TENANT_NAME_REPEAT);
    }

    private void checkTenantAdminPhoneUnique(String adminPhone, Long tenantId) {
        if (tenantId == null) {
            return;
        }
        Staff staff = staffMapper.selectOne(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getPhone, adminPhone)
                .eq(tenantId != null, Staff::getTenantId, tenantId)
                .eq(Staff::getLeaveFlag, YesNoEnum.NO.getCode()));
        AssertUtil.isNull(staff, ExceptionEnum.TENANT_ADMIN_PHONE_REPEAT);
    }

    private String genTenantAdminIdNum(Long tenantId) {
        String seed = String.valueOf(new Date().getTime()) + tenantId;
        if (seed.length() >= 18) {
            return seed.substring(0, 18);
        }
        StringBuilder idNum = new StringBuilder(seed);
        while (idNum.length() < 18) {
            idNum.append("0");
        }
        return idNum.toString();
    }

    private void copyRoleAuthByTemplate(Long targetRoleId, Long tenantId) {
        // 默认按“超级管理员(默认租户)”做模板，确保新租户管理员拥有全部业务权限
        Long defaultTenantId = 1L;
        Long templateRoleId = saasTenantConfig.getTenantAdmin().getTemplateRoleId();
        Role superAdminRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getTenantId, defaultTenantId)
                .eq(Role::getName, "超级管理员"));
        if (superAdminRole != null) {
            templateRoleId = superAdminRole.getId();
        }
        List<RoleAuth> templateRoleAuthList = roleAuthMapper.selectList(new LambdaQueryWrapper<RoleAuth>()
                .eq(RoleAuth::getRoleId, templateRoleId));
        List<Auth> authList;
        if (templateRoleAuthList.isEmpty()) {
            authList = authMapper.selectList(new LambdaQueryWrapper<Auth>().orderByAsc(Auth::getId));
        } else {
            List<Long> authIdList = templateRoleAuthList.stream().map(RoleAuth::getAuthId).collect(Collectors.toList());
            authList = authMapper.selectBatchIds(authIdList);
        }
        for (Auth auth : authList) {
            String url = auth.getUrl() == null ? "" : auth.getUrl();
            if (url.startsWith("/platform") || url.startsWith("/tenant/")) {
                continue;
            }
            // 避免重复插入
            Long exist = roleAuthMapper.selectCount(new LambdaQueryWrapper<RoleAuth>()
                    .eq(RoleAuth::getRoleId, targetRoleId)
                    .eq(RoleAuth::getAuthId, auth.getId()));
            if (exist != null && exist > 0) {
                continue;
            }
            RoleAuth roleAuth = new RoleAuth();
            roleAuth.setRoleId(targetRoleId);
            roleAuth.setAuthId(auth.getId());
            roleAuth.setTenantId(tenantId);
            roleAuthMapper.insert(roleAuth);
        }
    }

    private Staff getTenantAdmin(Long tenantId) {
        Role tenantAdminRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getTenantId, tenantId)
                .eq(Role::getName, TENANT_ADMIN_ROLE_NAME));
        if (tenantAdminRole == null) {
            return null;
        }
        return staffMapper.selectOne(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getTenantId, tenantId)
                .eq(Staff::getRoleId, tenantAdminRole.getId())
                .eq(Staff::getLeaveFlag, YesNoEnum.NO.getCode()));
    }

    private void ensurePlatformAdminAccountMigrated() {
        Long defaultTenantId = 1L;
        // 确保平台菜单权限存在
        Auth platformRoot = authMapper.selectOne(new LambdaQueryWrapper<Auth>()
                .eq(Auth::getUrl, "/platform"));
        if (platformRoot == null) {
            platformRoot = new Auth();
            platformRoot.setTenantId(defaultTenantId);
            platformRoot.setParentId(0L);
            platformRoot.setTitle("平台管理");
            platformRoot.setName("Platform");
            platformRoot.setPath("/platform");
            platformRoot.setIcon("base");
            platformRoot.setUrl("/platform");
            platformRoot.setType("MENU");
            platformRoot.setMethod("GET");
            authMapper.insert(platformRoot);
        }
        Auth platformTenant = authMapper.selectOne(new LambdaQueryWrapper<Auth>()
                .eq(Auth::getUrl, "/platform/tenant"));
        if (platformTenant == null) {
            platformTenant = new Auth();
            platformTenant.setTenantId(defaultTenantId);
            platformTenant.setParentId(platformRoot.getId());
            platformTenant.setTitle("租户管理");
            platformTenant.setName("PlatformTenant");
            platformTenant.setPath("tenant");
            platformTenant.setIcon("");
            platformTenant.setUrl("/platform/tenant");
            platformTenant.setType("MENU");
            platformTenant.setMethod("GET");
            authMapper.insert(platformTenant);
        }
        // 确保平台管理员角色存在
        Role platformRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getTenantId, defaultTenantId)
                .eq(Role::getName, PLATFORM_ADMIN_ROLE_NAME));
        if (platformRole == null) {
            platformRole = new Role();
            platformRole.setTenantId(defaultTenantId);
            platformRole.setName(PLATFORM_ADMIN_ROLE_NAME);
            roleMapper.insert(platformRole);
        }
        // 仅保留平台权限
        roleAuthMapper.delete(new LambdaQueryWrapper<RoleAuth>()
                .eq(RoleAuth::getRoleId, platformRole.getId()));
        RoleAuth roleAuth = new RoleAuth();
        roleAuth.setRoleId(platformRole.getId());
        roleAuth.setAuthId(platformTenant.getId());
        roleAuth.setTenantId(defaultTenantId);
        roleAuthMapper.insert(roleAuth);
        RoleAuth roleAuthRoot = new RoleAuth();
        roleAuthRoot.setRoleId(platformRole.getId());
        roleAuthRoot.setAuthId(platformRoot.getId());
        roleAuthRoot.setTenantId(defaultTenantId);
        roleAuthMapper.insert(roleAuthRoot);
        // 迁移配置手机号对应账号
        List<Staff> platformCandidates = staffMapper.selectList(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getTenantId, defaultTenantId)
                .eq(Staff::getPhone, saasTenantConfig.getPlatform().getAdmin().getPhone())
                .eq(Staff::getLeaveFlag, YesNoEnum.NO.getCode()));
        AssertUtil.notTrue(platformCandidates.isEmpty(), ExceptionEnum.PLATFORM_ADMIN_NOT_FOUND);
        AssertUtil.notTrue(platformCandidates.size() > 1, ExceptionEnum.PLATFORM_ADMIN_NOT_UNIQUE);
        Staff platformAdmin = platformCandidates.get(0);
        if (!Objects.equals(platformAdmin.getRoleId(), platformRole.getId())) {
            Staff update = new Staff();
            update.setId(platformAdmin.getId());
            update.setRoleId(platformRole.getId());
            staffMapper.updateById(update);
        }
    }
}
