package com.shanzhu.beadhouse.service;

import com.shanzhu.beadhouse.entity.base.Result;
import com.shanzhu.beadhouse.entity.query.EditTenantStatusQuery;
import com.shanzhu.beadhouse.entity.query.OperateTenantQuery;
import com.shanzhu.beadhouse.entity.query.PageTenantByKeyQuery;
import com.shanzhu.beadhouse.entity.query.ResetTenantAdminPassQuery;

public interface TenantService {
    Result pageTenantByKey(PageTenantByKeyQuery query);

    Result addTenant(OperateTenantQuery query);

    Result getTenantById(Long tenantId);

    Result editTenant(OperateTenantQuery query);

    Result editTenantStatus(EditTenantStatusQuery query);

    Result resetTenantAdminPass(ResetTenantAdminPassQuery query);
}
