package com.mgt.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductSchemaInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void expandProductTextColumns() {
        alterColumn("description", "TEXT");
        alterColumn("artisan_story", "TEXT");
        alterColumn("craft_process", "TEXT");
        alterColumn("rejection_reason", "TEXT");
        alterColumn("origin_area", "VARCHAR(500)");
        alterColumn("image", "VARCHAR(500)");
    }

    private void alterColumn(String columnName, String sqlType) {
        try {
            jdbcTemplate.execute("ALTER TABLE add_product MODIFY COLUMN " + columnName + " " + sqlType);
        } catch (Exception ex) {
            System.err.println("Product schema check skipped for column " + columnName + ": " + ex.getMessage());
        }
    }
}
