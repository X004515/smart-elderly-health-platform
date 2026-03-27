package com.shanzhu.beadhouse.common.config.tenant;

import com.shanzhu.beadhouse.common.constant.Constant;

public class TenantContextHolder {
    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_CODE = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> PLATFORM_ADMIN = new ThreadLocal<>();

    private TenantContextHolder() {
    }

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static Long getTenantIdOrDefault() {
        Long tenantId = TENANT_ID.get();
        return tenantId == null ? 1L : tenantId;
    }

    public static void setTenantCode(String tenantCode) {
        TENANT_CODE.set(tenantCode);
    }

    public static String getTenantCode() {
        String tenantCode = TENANT_CODE.get();
        return tenantCode == null ? Constant.DEFAULT_TENANT_CODE : tenantCode;
    }

    public static void setPlatformAdmin(Boolean platformAdmin) {
        PLATFORM_ADMIN.set(platformAdmin);
    }

    public static Boolean isPlatformAdmin() {
        return Boolean.TRUE.equals(PLATFORM_ADMIN.get());
    }

    public static void clear() {
        TENANT_ID.remove();
        TENANT_CODE.remove();
        PLATFORM_ADMIN.remove();
    }
}
