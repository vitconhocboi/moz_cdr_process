package com.cdr.util;

import com.cdr.model.SystemConfig;
import com.cdr.model.VoiceConfig;
import com.cdr.model.DataConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
        
        // Voice CDR folders
        config.setVoiceInputFolder(props.getProperty("cdr.voice.input.folder", "/data/input/voice"));
        config.setVoiceOutputFolder(props.getProperty("cdr.voice.output.folder", "/data/output/voice"));
        config.setVoiceBackupFolder(props.getProperty("cdr.voice.backup.folder", "/data/backup/voice"));
        config.setVoiceErrorFolder(props.getProperty("cdr.voice.error.folder", "/data/error/voice"));
        
        // Data CDR folders
        config.setDataInputFolder(props.getProperty("cdr.data.input.folder", "/data/input/data"));
        config.setDataOutputFolder(props.getProperty("cdr.data.output.folder", "/data/output/data"));
        config.setDataBackupFolder(props.getProperty("cdr.data.backup.folder", "/data/backup/data"));
        config.setDataErrorFolder(props.getProperty("cdr.data.error.folder", "/data/error/data"));
        
        // PCRF CDR folders
        config.setPcrfInputFolder(props.getProperty("cdr.pcrf.input.folder", "/data/input/pcrf"));
        config.setPcrfOutputFolder(props.getProperty("cdr.pcrf.output.folder", "/data/output/pcrf"));
        config.setPcrfBackupFolder(props.getProperty("cdr.pcrf.backup.folder", "/data/backup/pcrf"));
        config.setPcrfErrorFolder(props.getProperty("cdr.pcrf.error.folder", "/data/error/pcrf"));
        config.setVoiceSlaves(Integer.parseInt(props.getProperty("cdr.voice.slaves", "2")));
        config.setDataSlaves(Integer.parseInt(props.getProperty("cdr.data.slaves", "2")));
        config.setPcrfSlaves(Integer.parseInt(props.getProperty("cdr.pcrf.slaves", "2")));
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
    
    public static List<String> getDataAccounts() {
        Properties props = loadProperties("data-config.properties");
        String accounts = props.getProperty("cdr.data.accounts", "");
        return Arrays.asList(accounts.split(","));
    }
    
    public static Set<String> getDataExceptAccounts() {
        return getCachedDataConfig().exceptAccounts;
    }
    
    public static List<String> getPcrfAccounts() {
        Properties props = loadProperties("pcrf-config.properties");
        String accounts = props.getProperty("cdr.pcrf.accounts", "");
        return Arrays.asList(accounts.split(","));
    }
    
    public static double getDataReductionPercentage() {
        return getCachedDataConfig().reductionPercentage;
    }
    
    public static double getPcrfReductionPercentage() {
        Properties props = loadProperties("pcrf-config.properties");
        return Double.parseDouble(props.getProperty("cdr.pcrf.reduction.percentage", "30.0"));
    }
    
    /**
     * Load all voice configuration properties from voice-config.properties
     */
    public static VoiceConfig loadVoiceConfig() {
        Properties props = loadProperties("voice-config.properties");
        VoiceConfig config = new VoiceConfig();
        
        // Basic voice configuration
        config.voiceChargeAmount = Double.parseDouble(props.getProperty("cdr.voice.charge.amount", "0.0"));
        config.feeTypeMoneyValue = props.getProperty("feeTypeMoneyValue", "");
        config.amountRate = Double.parseDouble(props.getProperty("amountRate", "0.5"));
        config.callDurationPosition = Integer.parseInt(props.getProperty("callDurationPosition", "22"));
        
        // Basic CDR field positions (callingPartyNumberPosition and startTimePosition are inherited from BaseConfig)
        config.setCallingPartyNumberPosition(Integer.parseInt(props.getProperty("callingPartyNumberPosition", "4")));
        config.setStartTimePosition(Integer.parseInt(props.getProperty("startTimePosition", "20")));
        config.calledPartyNumberPosition = Integer.parseInt(props.getProperty("calledPartyNumberPosition", "5"));

        // Charge configuration
        config.ocsChargeCount = Integer.parseInt(props.getProperty("ocsChargeCount", "10"));
        config.specialChargeCount = Integer.parseInt(props.getProperty("specialChargeCount", "0"));
        
        // Special account types
        String accounts = props.getProperty("cdr.voice.special.accounts", "");
        if (!accounts.isEmpty()) {
            String[] accountList = accounts.split(",");
            for (String account : accountList) {
                account = account.trim();
                if (!account.isEmpty()) {
                    config.specialAccountTypes.add(account);
                }
            }
        }
        
        // Account type positions
        for (int i = 1; i <= config.ocsChargeCount; i++) {
            String accountTypePos = props.getProperty("AccountType" + i);
            String feeTypePos = props.getProperty("FeeType" + i);
            String chargeAmountPos = props.getProperty("ChargeAmount" + i);
            String currentAcctAmountPos = props.getProperty("CurrentAcctAmount" + i);
            
            if (accountTypePos != null) config.getAccountTypePositions().put("AccountType" + i, Integer.parseInt(accountTypePos));
            if (feeTypePos != null) config.getFeeTypePositions().put("FeeType" + i, Integer.parseInt(feeTypePos));
            if (chargeAmountPos != null) config.getChargeAmountPositions().put("ChargeAmount" + i, Integer.parseInt(chargeAmountPos));
            if (currentAcctAmountPos != null) config.getCurrentAcctAmountPositions().put("CurrentAcctAmount" + i, Integer.parseInt(currentAcctAmountPos));
        }
        
        return config;
    }
    
    // Cached voice config for performance
    private static VoiceConfig cachedVoiceConfig = null;
    
    private static VoiceConfig getCachedVoiceConfig() {
        if (cachedVoiceConfig == null) {
            cachedVoiceConfig = loadVoiceConfig();
        }
        return cachedVoiceConfig;
    }
    
    /**
     * Load all data configuration properties from data-config.properties
     */
    public static DataConfig loadDataConfig() {
        Properties props = loadProperties("data-config.properties");
        DataConfig config = new DataConfig();
        
        // Basic data configuration
        String exceptAccounts = props.getProperty("cdr.data.except.accounts", "");
        if (!exceptAccounts.isEmpty()) {
            String[] accountList = exceptAccounts.split(",");
            for (String account : accountList) {
                account = account.trim();
                if (!account.isEmpty()) {
                    config.exceptAccounts.add(account);
                }
            }
        }
        
        config.reductionPercentage = Double.parseDouble(props.getProperty("cdr.data.reduction.percentage", "50.0"));
        config.chargeCount = Integer.parseInt(props.getProperty("dataChargeCount", "10"));
        
        // Basic CDR field positions (inherited from BaseConfig)
        config.setStartTimePosition(Integer.parseInt(props.getProperty("dataStartTimePosition", "2")));
        config.setCallingPartyNumberPosition(Integer.parseInt(props.getProperty("dataCallingPartyNumberPosition", "4")));
        
        // Data CDR field positions
        config.totalFluxPosition = Integer.parseInt(props.getProperty("dataTotalFluxPosition", "16"));
        config.upFluxPosition = Integer.parseInt(props.getProperty("dataUpFluxPosition", "17"));
        config.downFluxPosition = Integer.parseInt(props.getProperty("dataDownFluxPosition", "18"));
        config.totalChargeFluxPosition = Integer.parseInt(props.getProperty("dataTotalChargeFluxPosition", "57"));
        
        // Account type and fee positions (for 10 accounts)
        for (int i = 1; i <= config.chargeCount; i++) {
            String accountTypePos = props.getProperty("dataAccountType" + i);
            String feeTypePos = props.getProperty("dataFeeType" + i);
            String chargeAmountPos = props.getProperty("dataChargeAmount" + i);
            String currentAcctAmountPos = props.getProperty("dataCurrentAcctAmount" + i);
            
            if (accountTypePos != null) config.getAccountTypePositions().put("dataAccountType" + i, Integer.parseInt(accountTypePos));
            if (feeTypePos != null) config.getFeeTypePositions().put("dataFeeType" + i, Integer.parseInt(feeTypePos));
            if (chargeAmountPos != null) config.getChargeAmountPositions().put("dataChargeAmount" + i, Integer.parseInt(chargeAmountPos));
            if (currentAcctAmountPos != null) config.getCurrentAcctAmountPositions().put("dataCurrentAcctAmount" + i, Integer.parseInt(currentAcctAmountPos));
        }
        
        return config;
    }
    
    // Cached data config for performance
    private static DataConfig cachedDataConfig = null;
    
    private static DataConfig getCachedDataConfig() {
        if (cachedDataConfig == null) {
            cachedDataConfig = loadDataConfig();
        }
        return cachedDataConfig;
    }
    
    // PCRF configuration methods
    public static int getPcrfChargeCount() {
        Properties props = loadProperties("pcrf-config.properties");
        return Integer.parseInt(props.getProperty("pcrfChargeCount", "5"));
    }
    
    public static int getPcrfSpecialChargeCount() {
        Properties props = loadProperties("pcrf-config.properties");
        return Integer.parseInt(props.getProperty("pcrfSpecialChargeCount", "2"));
    }
    
    public static Map<String, String> getPcrfSpecialAccountTypes() {
        Properties props = loadProperties("pcrf-config.properties");
        Map<String, String> specialAccounts = new HashMap<>();
        int count = getPcrfSpecialChargeCount();
        
        for (int i = 1; i <= count; i++) {
            String accountType = props.getProperty("pcrfSpecialAccountType" + i);
            String discountRate = props.getProperty("pcrfDiscountRate" + i);
            if (accountType != null) {
                specialAccounts.put(accountType, discountRate != null ? discountRate : "0");
            }
        }
        return specialAccounts;
    }
    
    public static Map<String, Integer> getPcrfAccountTypePositions() {
        Properties props = loadProperties("pcrf-config.properties");
        Map<String, Integer> positions = new HashMap<>();
        int count = getPcrfChargeCount();
        
        for (int i = 1; i <= count; i++) {
            String accountTypePos = props.getProperty("pcrfAccountType" + i);
            String feeTypePos = props.getProperty("pcrfFeeType" + i);
            String chargeAmountPos = props.getProperty("pcrfChargeAmount" + i);
            String currentAcctAmountPos = props.getProperty("pcrfCurrentAcctAmount" + i);
            
            if (accountTypePos != null) positions.put("pcrfAccountType" + i, Integer.parseInt(accountTypePos));
            if (feeTypePos != null) positions.put("pcrfFeeType" + i, Integer.parseInt(feeTypePos));
            if (chargeAmountPos != null) positions.put("pcrfChargeAmount" + i, Integer.parseInt(chargeAmountPos));
            if (currentAcctAmountPos != null) positions.put("pcrfCurrentAcctAmount" + i, Integer.parseInt(currentAcctAmountPos));
        }
        return positions;
    }
    
    public static Map<String, Integer> getPcrfFieldPositions() {
        Properties props = loadProperties("pcrf-config.properties");
        Map<String, Integer> positions = new HashMap<>();
        
        positions.put("totalFlux", Integer.parseInt(props.getProperty("pcrfTotalFluxPosition", "15")));
        positions.put("upFlux", Integer.parseInt(props.getProperty("pcrfUpFluxPosition", "16")));
        positions.put("downFlux", Integer.parseInt(props.getProperty("pcrfDownFluxPosition", "17")));
        positions.put("totalChargeFlux", Integer.parseInt(props.getProperty("pcrfTotalChargeFluxPosition", "18")));
        
        return positions;
    }
    
    public static String getReportFolder() {
        Properties props = loadProperties();
        return props.getProperty("cdr.report.folder", "data/reports");
    }
    
    public static int getBatchSize() {
        Properties props = loadProperties();
        return Integer.parseInt(props.getProperty("cdr.batch.size", "500"));
    }
    
    private static Properties loadProperties() {
        return loadProperties("application.properties");
    }
    
    private static Properties loadProperties(String fileName) {
        Properties props = new Properties();
        try (InputStream is = ConfigUtils.class.getClassLoader()
                .getResourceAsStream(fileName)) {
            props.load(is);
        } catch (IOException e) {
            log.error("Failed to load {}", fileName, e);
        }
        return props;
    }
}
