package com.cdr.model;

import java.util.Properties;

/**
 * System configuration class for CDR processing system
 */
public class SystemConfig {
    // Voice CDR folders
    private String voiceInputFolder;
    private String voiceOutputFolder;
    private String voiceBackupFolder;
    private String voiceErrorFolder;
    private String voiceConfigPath;
    
    // Data CDR folders
    private String dataInputFolder;
    private String dataOutputFolder;
    private String dataBackupFolder;
    private String dataErrorFolder;
    private String dataConfigPath;

    // PCRF CDR folders
    private String pcrfInputFolder;
    private String pcrfOutputFolder;
    private String pcrfBackupFolder;
    private String pcrfErrorFolder;
    private String pcrfConfigPath;
    
    // Slave configurations
    private int voiceSlaves;
    private int dataSlaves;
    private int pcrfSlaves;
    
    // Processing configurations
    private int batchSize;
    private long processingInterval;
    private String reportFolder;
    
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
        this.processingInterval = 60000;
        this.heartbeatInterval = 30000;
        this.heartbeatTimeout = 300000;
        this.databasePoolSize = 10;
        this.databasePoolMax = 20;
        this.loggingLevel = "INFO";
        this.loggingMaxSize = "100MB";
        this.loggingMaxFiles = 10;
    }

    public String getReportFolder() {
        return reportFolder;
    }

    public void setReportFolder(String reportFolder) {
        this.reportFolder = reportFolder;
    }

    public String getVoiceConfigPath() {
        return voiceConfigPath;
    }

    public void setVoiceConfigPath(String voiceConfigPath) {
        this.voiceConfigPath = voiceConfigPath;
    }

    public String getDataConfigPath() {
        return dataConfigPath;
    }

    public void setDataConfigPath(String dataConfigPath) {
        this.dataConfigPath = dataConfigPath;
    }

    public String getPcrfConfigPath() {
        return pcrfConfigPath;
    }

    public void setPcrfConfigPath(String pcrfConfigPath) {
        this.pcrfConfigPath = pcrfConfigPath;
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
    
    public int getPcrfSlaves() {
        return pcrfSlaves;
    }
    
    public void setPcrfSlaves(int pcrfSlaves) {
        this.pcrfSlaves = pcrfSlaves;
    }
    
    public int getBatchSize() {
        return batchSize;
    }
    
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
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
    
    // Voice CDR folder getters and setters
    public String getVoiceInputFolder() {
        return voiceInputFolder;
    }
    
    public void setVoiceInputFolder(String voiceInputFolder) {
        this.voiceInputFolder = voiceInputFolder;
    }
    
    public String getVoiceOutputFolder() {
        return voiceOutputFolder;
    }
    
    public void setVoiceOutputFolder(String voiceOutputFolder) {
        this.voiceOutputFolder = voiceOutputFolder;
    }
    
    public String getVoiceBackupFolder() {
        return voiceBackupFolder;
    }
    
    public void setVoiceBackupFolder(String voiceBackupFolder) {
        this.voiceBackupFolder = voiceBackupFolder;
    }
    
    public String getVoiceErrorFolder() {
        return voiceErrorFolder;
    }
    
    public void setVoiceErrorFolder(String voiceErrorFolder) {
        this.voiceErrorFolder = voiceErrorFolder;
    }
    
    // Data CDR folder getters and setters
    public String getDataInputFolder() {
        return dataInputFolder;
    }
    
    public void setDataInputFolder(String dataInputFolder) {
        this.dataInputFolder = dataInputFolder;
    }
    
    public String getDataOutputFolder() {
        return dataOutputFolder;
    }
    
    public void setDataOutputFolder(String dataOutputFolder) {
        this.dataOutputFolder = dataOutputFolder;
    }
    
    public String getDataBackupFolder() {
        return dataBackupFolder;
    }
    
    public void setDataBackupFolder(String dataBackupFolder) {
        this.dataBackupFolder = dataBackupFolder;
    }
    
    public String getDataErrorFolder() {
        return dataErrorFolder;
    }
    
    public void setDataErrorFolder(String dataErrorFolder) {
        this.dataErrorFolder = dataErrorFolder;
    }
    
    // PCRF CDR folder getters and setters
    public String getPcrfInputFolder() {
        return pcrfInputFolder;
    }
    
    public void setPcrfInputFolder(String pcrfInputFolder) {
        this.pcrfInputFolder = pcrfInputFolder;
    }
    
    public String getPcrfOutputFolder() {
        return pcrfOutputFolder;
    }
    
    public void setPcrfOutputFolder(String pcrfOutputFolder) {
        this.pcrfOutputFolder = pcrfOutputFolder;
    }
    
    public String getPcrfBackupFolder() {
        return pcrfBackupFolder;
    }
    
    public void setPcrfBackupFolder(String pcrfBackupFolder) {
        this.pcrfBackupFolder = pcrfBackupFolder;
    }
    
    public String getPcrfErrorFolder() {
        return pcrfErrorFolder;
    }
    
    public void setPcrfErrorFolder(String pcrfErrorFolder) {
        this.pcrfErrorFolder = pcrfErrorFolder;
    }
}
