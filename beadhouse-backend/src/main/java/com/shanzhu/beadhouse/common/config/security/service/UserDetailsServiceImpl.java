package com.shanzhu.beadhouse.common.config.security.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanzhu.beadhouse.common.config.security.entity.LoginUserDetails;
import com.shanzhu.beadhouse.common.config.tenant.SaasTenantConfig;
import com.shanzhu.beadhouse.common.constant.Constant;
import com.shanzhu.beadhouse.common.constant.ExceptionEnum;
import com.shanzhu.beadhouse.common.util.AssertUtil;
import com.shanzhu.beadhouse.dao.mapper.AuthMapper;
import com.shanzhu.beadhouse.dao.mapper.TenantMapper;
import com.shanzhu.beadhouse.entity.po.Auth;
import com.shanzhu.beadhouse.entity.po.Staff;
import com.shanzhu.beadhouse.entity.po.Tenant;
import com.shanzhu.beadhouse.entity.vo.LoginUserVo;
import com.shanzhu.beadhouse.service.common.StaffFunc;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 自定义数据库查询
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private StaffFunc staffFunc;
    @Resource
    private AuthMapper authMapper;
    @Resource
    private TenantMapper tenantMapper;
    @Resource
    private SaasTenantConfig saasTenantConfig;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 登录名格式：tenantCode|account
        String tenantCode = Constant.DEFAULT_TENANT_CODE;
        String account = username;
        if (username.contains("|")) {
            String[] split = username.split("\\|", 2);
            tenantCode = split[0];
            account = split[1];
        }
        Tenant tenant = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getTenantCode, tenantCode));
        AssertUtil.notNull(tenant, ExceptionEnum.TENANT_NOT_EXIST);
        AssertUtil.isTrue("Y".equals(tenant.getActiveFlag()), ExceptionEnum.TENANT_DISABLED);
        // 查询基本信息
        Staff staff = staffFunc.getStaffByAccount(tenant.getId(), account);
        // 未查询到数据
        AssertUtil.notNull(staff, ExceptionEnum.CERTIFICATION_ERROR);
        // 查询权限信息
        List<Long> authIdList = new ArrayList<>();
        List<String> authUrlList = new ArrayList<>();
        for (Auth auth : authMapper.listAuthByRoleId(staff.getRoleId())) {
            authIdList.add(auth.getId());
            authUrlList.add(auth.getUrl());
        }
        boolean forcePlatformAdmin = tenant.getId().equals(1L)
                && Objects.equals(staff.getPhone(), saasTenantConfig.getPlatform().getAdmin().getPhone());
        if (forcePlatformAdmin) {
            List<Auth> platformAuthList = authMapper.selectList(new LambdaQueryWrapper<Auth>()
                    .in(Auth::getUrl, Arrays.asList("/platform", "/platform/tenant")));
            authIdList.clear();
            authUrlList.clear();
            for (Auth auth : platformAuthList) {
                authIdList.add(auth.getId());
                authUrlList.add(auth.getUrl());
            }
        }
        // 把数据封装成UserDetails返回
        LoginUserVo loginUserVo = BeanUtil.toBean(staff, LoginUserVo.class);
        loginUserVo.setAuthIdList(authIdList);
        loginUserVo.setAuthUrlList(authUrlList);
        loginUserVo.setTenantId(tenant.getId());
        loginUserVo.setTenantCode(tenantCode);
        boolean platformAdmin = authUrlList.contains("/platform/tenant") || forcePlatformAdmin;
        loginUserVo.setPlatformAdmin(platformAdmin);
        return new LoginUserDetails(loginUserVo);
    }
}
