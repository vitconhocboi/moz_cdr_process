package com.cdr.util;

import com.cdr.model.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Configuration utility class for loading system configuration
 */
public class ConfigUtils {
    
    private static final Logger log = LoggerFactory.getLogger(ConfigUtils.class);
    
    public static SystemConfig loadSystemConfig() {
        Properties props = new Properties();
        try (InputStream is = ConfigUtils.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            props.load(is);
        } catch (IOException e) {
            log.error("Failed to load application.properties", e);
            throw new RuntimeException("Configuration loading failed", e);
        }
        
        SystemConfig config = new SystemConfig();
        config.setInputFolder(props.getProperty("cdr.input.folder", "/data/input"));
        config.setOutputFolder(props.getProperty("cdr.output.folder", "/data/output"));
        config.setBackupFolder(props.getProperty("cdr.backup.folder", "/data/backup"));
        config.setErrorFolder(props.getProperty("cdr.error.folder", "/data/error"));
        config.setVoiceSlaves(Integer.parseInt(props.getProperty("cdr.voice.slaves", "2")));
        config.setDataSlaves(Integer.parseInt(props.getProperty("cdr.data.slaves", "2")));
        config.setBatchSize(Integer.parseInt(props.getProperty("cdr.batch.size", "1000")));
        config.setThreadPoolSize(Integer.parseInt(props.getProperty("cdr.thread.pool.size", "10")));
        config.setServerName(props.getProperty("cdr.server.name", "cdr-server-01"));
        config.setHeartbeatInterval(Long.parseLong(props.getProperty("cdr.heartbeat.interval", "30000")));
        config.setHeartbeatTimeout(Long.parseLong(props.getProperty("cdr.heartbeat.timeout", "300000")));
        config.setProcessingInterval(Long.parseLong(props.getProperty("cdr.processing.interval", "60000")));
        
        // Database configuration
        config.setDatabaseUrl(props.getProperty("cdr.database.url"));
        config.setDatabaseUsername(props.getProperty("cdr.database.username"));
        config.setDatabasePassword(props.getProperty("cdr.database.password"));
        config.setDatabaseDriver(props.getProperty("cdr.database.driver"));
        config.setDatabasePoolSize(Integer.parseInt(props.getProperty("cdr.database.pool.size", "10")));
        config.setDatabasePoolMax(Integer.parseInt(props.getProperty("cdr.database.pool.max", "20")));
        
        // Logging configuration
        config.setLoggingLevel(props.getProperty("cdr.logging.level", "INFO"));
        config.setLoggingFile(props.getProperty("cdr.logging.file", "/var/log/cdr-processor/cdr-processor.log"));
        config.setLoggingMaxSize(props.getProperty("cdr.logging.max.size", "100MB"));
        config.setLoggingMaxFiles(Integer.parseInt(props.getProperty("cdr.logging.max.files", "10")));
        
        return config;
    }
    
    public static List<String> getVoiceAccounts() {
        Properties props = loadProperties();
        String accounts = props.getProperty("cdr.voice.accounts", "");
        return Arrays.asList(accounts.split(","));
    }
    
    public static List<String> getDataAccounts() {
        Properties props = loadProperties();
        String accounts = props.getProperty("cdr.data.accounts", "");
        return Arrays.asList(accounts.split(","));
    }
    
    public static double getReductionPercentage() {
        Properties props = loadProperties();
        return Double.parseDouble(props.getProperty("cdr.data.reduction.percentage", "50.0"));
    }
    
    public static double getChargeAmount() {
        Properties props = loadProperties();
        return Double.parseDouble(props.getProperty("cdr.voice.charge.amount", "0.0"));
    }
    
    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = ConfigUtils.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            props.load(is);
        } catch (IOException e) {
            log.error("Failed to load application.properties", e);
        }
        return props;
    }
}
