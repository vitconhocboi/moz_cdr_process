package com.cdr.model;

import java.util.Properties;

/**
 * System configuration class for CDR processing system
 */
public class SystemConfig {
    
    // Folder configurations
    private String inputFolder;
    private String outputFolder;
    private String backupFolder;
    private String errorFolder;
    
    // Slave configurations
    private int voiceSlaves;
    private int dataSlaves;
    
    // Processing configurations
    private int batchSize;
    private int threadPoolSize;
    private long processingInterval;
    
    // High availability configurations
    private String serverName;
    private long heartbeatInterval;
    private long heartbeatTimeout;
    
    // Database configurations
    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;
    private String databaseDriver;
    private int databasePoolSize;
    private int databasePoolMax;
    
    // Logging configurations
    private String loggingLevel;
    private String loggingFile;
    private String loggingMaxSize;
    private int loggingMaxFiles;
    
    public SystemConfig() {
        // Default values
        this.voiceSlaves = 2;
        this.dataSlaves = 2;
        this.batchSize = 1000;
        this.threadPoolSize = 10;
        this.processingInterval = 60000;
        this.heartbeatInterval = 30000;
        this.heartbeatTimeout = 300000;
        this.databasePoolSize = 10;
        this.databasePoolMax = 20;
        this.loggingLevel = "INFO";
        this.loggingMaxSize = "100MB";
        this.loggingMaxFiles = 10;
    }
    
    // Getters and Setters
    public String getInputFolder() {
        return inputFolder;
    }
    
    public void setInputFolder(String inputFolder) {
        this.inputFolder = inputFolder;
    }
    
    public String getOutputFolder() {
        return outputFolder;
    }
    
    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }
    
    public String getBackupFolder() {
        return backupFolder;
    }
    
    public void setBackupFolder(String backupFolder) {
        this.backupFolder = backupFolder;
    }
    
    public String getErrorFolder() {
        return errorFolder;
    }
    
    public void setErrorFolder(String errorFolder) {
        this.errorFolder = errorFolder;
    }
    
    public int getVoiceSlaves() {
        return voiceSlaves;
    }
    
    public void setVoiceSlaves(int voiceSlaves) {
        this.voiceSlaves = voiceSlaves;
    }
    
    public int getDataSlaves() {
        return dataSlaves;
    }
    
    public void setDataSlaves(int dataSlaves) {
        this.dataSlaves = dataSlaves;
    }
    
    public int getBatchSize() {
        return batchSize;
    }
    
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
    
    public int getThreadPoolSize() {
        return threadPoolSize;
    }
    
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
    
    public long getProcessingInterval() {
        return processingInterval;
    }
    
    public void setProcessingInterval(long processingInterval) {
        this.processingInterval = processingInterval;
    }
    
    public String getServerName() {
        return serverName;
    }
    
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    
    public long getHeartbeatInterval() {
        return heartbeatInterval;
    }
    
    public void setHeartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
    
    public long getHeartbeatTimeout() {
        return heartbeatTimeout;
    }
    
    public void setHeartbeatTimeout(long heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }
    
    public String getDatabaseUrl() {
        return databaseUrl;
    }
    
    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }
    
    public String getDatabaseUsername() {
        return databaseUsername;
    }
    
    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }
    
    public String getDatabasePassword() {
        return databasePassword;
    }
    
    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }
    
    public String getDatabaseDriver() {
        return databaseDriver;
    }
    
    public void setDatabaseDriver(String databaseDriver) {
        this.databaseDriver = databaseDriver;
    }
    
    public int getDatabasePoolSize() {
        return databasePoolSize;
    }
    
    public void setDatabasePoolSize(int databasePoolSize) {
        this.databasePoolSize = databasePoolSize;
    }
    
    public int getDatabasePoolMax() {
        return databasePoolMax;
    }
    
    public void setDatabasePoolMax(int databasePoolMax) {
        this.databasePoolMax = databasePoolMax;
    }
    
    public String getLoggingLevel() {
        return loggingLevel;
    }
    
    public void setLoggingLevel(String loggingLevel) {
        this.loggingLevel = loggingLevel;
    }
    
    public String getLoggingFile() {
        return loggingFile;
    }
    
    public void setLoggingFile(String loggingFile) {
        this.loggingFile = loggingFile;
    }
    
    public String getLoggingMaxSize() {
        return loggingMaxSize;
    }
    
    public void setLoggingMaxSize(String loggingMaxSize) {
        this.loggingMaxSize = loggingMaxSize;
    }
    
    public int getLoggingMaxFiles() {
        return loggingMaxFiles;
    }
    
    public void setLoggingMaxFiles(int loggingMaxFiles) {
        this.loggingMaxFiles = loggingMaxFiles;
    }
}
