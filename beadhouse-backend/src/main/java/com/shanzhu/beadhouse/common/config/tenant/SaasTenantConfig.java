package com.shanzhu.beadhouse.common.config.tenant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "saas")
public class SaasTenantConfig {
    private Platform platform = new Platform();
    private TenantAdmin tenantAdmin = new TenantAdmin();

    @Data
    public static class Platform {
        private Admin admin = new Admin();

        @Data
        public static class Admin {
            private String phone = "13547584400";
        }
    }

    @Data
    public static class TenantAdmin {
        private String defaultPass = "123456";
        private Long templateRoleId = 1L;
    }
}
