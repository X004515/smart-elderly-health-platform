import { http } from '@/utils'

export function pageTenantByKey(params: any) {
  return http.get('/api/tenant/pageTenantByKey', { params })
}

export function addTenant(data: any) {
  return http.post('/api/tenant/addTenant', data)
}

export function getTenantById(params: { tenantId: number }) {
  return http.get('/api/tenant/getTenantById', { params })
}

export function editTenant(data: any) {
  return http.put('/api/tenant/editTenant', data)
}

export function editTenantStatus(data: { tenantId: number; activeFlag: string }) {
  return http.put('/api/tenant/editTenantStatus', data)
}

export function resetTenantAdminPass(data: { tenantId: number; newPass?: string }) {
  return http.put('/api/tenant/resetTenantAdminPass', data)
}
