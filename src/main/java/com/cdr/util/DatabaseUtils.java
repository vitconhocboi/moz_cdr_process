package com.cdr.util;

import com.cdr.model.SystemConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Database utility class for creating database connections
 */
public class DatabaseUtils {
    
    private static final Logger log = LoggerFactory.getLogger(DatabaseUtils.class);
    
    /**
     * Create HikariCP DataSource
     */
    public static DataSource createDataSource(SystemConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        
        hikariConfig.setJdbcUrl(config.getDatabaseUrl());
        hikariConfig.setUsername(config.getDatabaseUsername());
        hikariConfig.setPassword(config.getDatabasePassword());
        hikariConfig.setDriverClassName(config.getDatabaseDriver());
        
        // Connection pool settings
        hikariConfig.setMinimumIdle(config.getDatabasePoolSize());
        hikariConfig.setMaximumPoolSize(config.getDatabasePoolMax());
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setLeakDetectionThreshold(60000);
        
        // Connection pool name
        hikariConfig.setPoolName("CDRProcessorPool");
        
        try {
            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            log.info("Database connection pool created successfully");
            return dataSource;
        } catch (Exception e) {
            log.error("Failed to create database connection pool", e);
            throw new RuntimeException("Database connection failed", e);
        }
    }
}
