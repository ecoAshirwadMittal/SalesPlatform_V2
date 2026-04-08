package com.ecoatm.salesplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ecoATM Sales Platform — modern Spring Boot backend.
 *
 * Migrates from Mendix SalesPlatform to Next.js + Spring Boot + PostgreSQL.
 * Database schemas are managed by Flyway (db/migration/V*.sql).
 *
 * Module → Schema mapping:
 *   system + administration      → identity schema
 *   ecoatm_usermanagement        → user_mgmt schema
 *   ecoatm_buyermanagement       → buyer_mgmt schema
 *   saml20 + forgotpassword      → sso schema
 */
@SpringBootApplication
public class SalesPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesPlatformApplication.class, args);
    }
}
