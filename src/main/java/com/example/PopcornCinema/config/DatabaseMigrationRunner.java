package com.example.PopcornCinema.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMigrationRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrationRunner.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensurePromotionIdOnPaymentTransactions();
    }

    private void ensurePromotionIdOnPaymentTransactions() {
        try {
            Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = 'payment_transactions'
                  AND COLUMN_NAME = 'promotion_id'
            """, Integer.class);

            if (count != null && count == 0) {
                jdbcTemplate.execute("ALTER TABLE payment_transactions ADD COLUMN promotion_id BIGINT NULL");
                logger.info("Added column payment_transactions.promotion_id");
            }
        } catch (Exception ex) {
            logger.warn("Could not verify/add payment_transactions.promotion_id: {}", ex.getMessage());
        }
    }
}
