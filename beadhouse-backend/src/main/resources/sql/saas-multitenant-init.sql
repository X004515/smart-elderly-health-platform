-- SaaS 多租户初始化脚本（MySQL 8+）
-- 使用方法：
-- 1) 先确认当前数据库为 yly
-- 2) 按需修改 @platform_phone
-- 3) 全量执行

SET @platform_phone = '13547584400';

-- 1) 确保默认租户存在
INSERT INTO sys_tenant (
  tenant_code, tenant_name, active_flag, create_id, create_time, update_id, update_time
)
SELECT 'default', 'Default Tenant', 'Y', 1, NOW(), 1, NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM sys_tenant WHERE tenant_code = 'default'
);

SET @default_tenant_id = (
  SELECT id FROM sys_tenant WHERE tenant_code = 'default' LIMIT 1
);

-- 2) 补平台菜单权限
INSERT INTO auth (
  parent_id, title, name, path, icon, url, type, method, create_id, create_time, update_id, update_time, tenant_id
)
SELECT 0, '平台管理', 'Platform', '/platform', 'base', '/platform', 'MENU', 'GET', 1, NOW(), 1, NOW(), @default_tenant_id
WHERE NOT EXISTS (
  SELECT 1 FROM auth WHERE url = '/platform'
);

SET @platform_root_auth_id = (
  SELECT id FROM auth WHERE url = '/platform' LIMIT 1
);

INSERT INTO auth (
  parent_id, title, name, path, icon, url, type, method, create_id, create_time, update_id, update_time, tenant_id
)
SELECT @platform_root_auth_id, '租户管理', 'PlatformTenant', 'tenant', '', '/platform/tenant', 'MENU', 'GET', 1, NOW(), 1, NOW(), @default_tenant_id
WHERE NOT EXISTS (
  SELECT 1 FROM auth WHERE url = '/platform/tenant'
);

-- 兼容旧权限 URL，避免历史数据授权失效
SET @platform_tenant_auth_id = (
  SELECT id FROM auth WHERE url = '/platform/tenant' LIMIT 1
);
SET @legacy_tenant_auth_id = (
  SELECT id FROM auth WHERE url = '/tenant/pageTenantByKey' LIMIT 1
);

-- 3) 创建平台管理员角色
INSERT INTO role (name, create_id, create_time, update_id, update_time, tenant_id)
SELECT '平台管理员', 1, NOW(), 1, NOW(), @default_tenant_id
WHERE NOT EXISTS (
  SELECT 1 FROM role WHERE name = '平台管理员' AND tenant_id = @default_tenant_id
);

SET @platform_role_id = (
  SELECT id FROM role WHERE name = '平台管理员' AND tenant_id = @default_tenant_id LIMIT 1
);

-- 4) 平台角色只保留平台相关权限
DELETE FROM role_auth WHERE role_id = @platform_role_id;

INSERT INTO role_auth (role_id, auth_id, create_id, create_time, update_id, update_time, tenant_id)
SELECT @platform_role_id, id, 1, NOW(), 1, NOW(), @default_tenant_id
FROM auth
WHERE id IN (@platform_root_auth_id, @platform_tenant_auth_id)
   OR (id = @legacy_tenant_auth_id AND @legacy_tenant_auth_id IS NOT NULL);

-- 5) 迁移平台管理员账号（按手机号）
UPDATE staff
SET role_id = @platform_role_id, update_id = 1, update_time = NOW()
WHERE phone = @platform_phone
  AND tenant_id = @default_tenant_id
  AND leave_flag = 'N';

-- 6) 历史数据归并到 default 租户（仅填充 tenant_id 为空的数据）
-- 注意：PREPARE 一次只能执行一条 SQL，这里用游标逐表执行
DELIMITER $$
DROP PROCEDURE IF EXISTS fill_tenant_id$$
CREATE PROCEDURE fill_tenant_id()
BEGIN
  DECLARE done INT DEFAULT 0;
  DECLARE v_table_name VARCHAR(128);
  DECLARE cur CURSOR FOR
    SELECT DISTINCT table_name
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND column_name = 'tenant_id';
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO v_table_name;
    IF done = 1 THEN
      LEAVE read_loop;
    END IF;

    SET @tenant_fill_sql = CONCAT(
      'UPDATE `', v_table_name, '` SET tenant_id = ', @default_tenant_id, ' WHERE tenant_id IS NULL'
    );
    PREPARE stmt FROM @tenant_fill_sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END LOOP;
  CLOSE cur;
END$$
DELIMITER ;

CALL fill_tenant_id();
DROP PROCEDURE IF EXISTS fill_tenant_id;
