package com.mgt.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SellerSchemaInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void expandSellerColumns() {
        alterColumn("address", "VARCHAR(500)");
        alterColumn("artisan_story", "TEXT");
        alterColumn("craft_process", "TEXT");
        alterColumn("rejection_reason", "TEXT");
        alterColumn("nid_no", "VARCHAR(50)");
        alterColumn("phone", "VARCHAR(30)");
        alterColumn("email", "VARCHAR(150)");
        alterColumn("district", "VARCHAR(120)");
        alterColumn("status", "VARCHAR(30)");
        addColumnIfMissing("product_category", "VARCHAR(120)");
        addColumnIfMissing("business_type", "VARCHAR(80)");
        addColumnIfMissing("payment_method", "VARCHAR(50)");
        addColumnIfMissing("payment_number", "VARCHAR(80)");
        addColumnIfMissing("profile_photo", "VARCHAR(255)");
        addColumnIfMissing("nid_front_image", "VARCHAR(255)");
        addColumnIfMissing("nid_back_image", "VARCHAR(255)");
    }

    private void addColumnIfMissing(String columnName, String sqlType) {
        try {
            jdbcTemplate.execute("ALTER TABLE seller ADD COLUMN " + columnName + " " + sqlType);
        } catch (Exception ex) {
            System.err.println("Seller schema add column skipped for " + columnName + ": " + ex.getMessage());
        }
    }

    private void alterColumn(String columnName, String sqlType) {
        try {
            jdbcTemplate.execute("ALTER TABLE seller MODIFY COLUMN " + columnName + " " + sqlType);
        } catch (Exception ex) {
            System.err.println("Seller schema check skipped for column " + columnName + ": " + ex.getMessage());
        }
    }
}
