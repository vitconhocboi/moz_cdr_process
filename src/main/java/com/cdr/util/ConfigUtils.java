package com.cdr.util;

import com.cdr.model.SystemConfig;
import com.cdr.model.VoiceConfig;
import com.cdr.model.DataConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
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
        return loadSystemConfig("application.properties");
    }

    public static SystemConfig loadSystemConfig(String configFile) {
        Properties props = loadProperties(configFile);


        SystemConfig config = new SystemConfig();

        config.setVoiceConfigPath(props.getProperty("cdr.voice.config.path", "voice-config.properties"));
        config.setDataConfigPath(props.getProperty("cdr.data.config.path", "data-config.properties"));
        config.setPcrfConfigPath(props.getProperty("cdr.pcrf.config.path", "pcrf-config.properties"));
        config.setReportFolder(props.getProperty("cdr.report.folder", "data/reports"));
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

    /**
     * Load all voice configuration properties from voice-config.properties
     */
    public static VoiceConfig loadVoiceConfig(SystemConfig systemConfig) {
        Properties props = loadProperties(systemConfig.getVoiceConfigPath());
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
        config.ocsChargeCount = Integer.parseInt(props.getProperty("chargeCount", "10"));
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

            if (accountTypePos != null)
                config.getAccountTypePositions().put("AccountType" + i, Integer.parseInt(accountTypePos));
            if (feeTypePos != null) config.getFeeTypePositions().put("FeeType" + i, Integer.parseInt(feeTypePos));
            if (chargeAmountPos != null)
                config.getChargeAmountPositions().put("ChargeAmount" + i, Integer.parseInt(chargeAmountPos));
            if (currentAcctAmountPos != null)
                config.getCurrentAcctAmountPositions().put("CurrentAcctAmount" + i, Integer.parseInt(currentAcctAmountPos));
        }

        return config;
    }

    /**
     * Load all data configuration properties from data-config.properties
     */
    public static DataConfig loadDataConfig(SystemConfig systemConfig) {
        Properties props = loadProperties(systemConfig.getDataConfigPath());
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
        config.chargeCount = Integer.parseInt(props.getProperty("chargeCount", "10"));

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

            if (accountTypePos != null)
                config.getAccountTypePositions().put("dataAccountType" + i, Integer.parseInt(accountTypePos));
            if (feeTypePos != null) config.getFeeTypePositions().put("dataFeeType" + i, Integer.parseInt(feeTypePos));
            if (chargeAmountPos != null)
                config.getChargeAmountPositions().put("dataChargeAmount" + i, Integer.parseInt(chargeAmountPos));
            if (currentAcctAmountPos != null)
                config.getCurrentAcctAmountPositions().put("dataCurrentAcctAmount" + i, Integer.parseInt(currentAcctAmountPos));
        }

        return config;
    }

    public static DataConfig loadPcrfConfig(SystemConfig systemConfig) {
        Properties props = loadProperties(systemConfig.getPcrfConfigPath());
        DataConfig config = new DataConfig();

        // Basic data configuration
        String exceptAccounts = props.getProperty("cdr.pcrf.except.accounts", "");
        if (!exceptAccounts.isEmpty()) {
            String[] accountList = exceptAccounts.split(",");
            for (String account : accountList) {
                account = account.trim();
                if (!account.isEmpty()) {
                    config.exceptAccounts.add(account);
                }
            }
        }

        config.reductionPercentage = Double.parseDouble(props.getProperty("cdr.pcrf.reduction.percentage", "50.0"));
        config.chargeCount = Integer.parseInt(props.getProperty("pcrfChargeCount", "10"));

        // Basic CDR field positions (inherited from BaseConfig)
        config.setStartTimePosition(Integer.parseInt(props.getProperty("pcrfStartTimePosition", "2")));
        config.setCallingPartyNumberPosition(Integer.parseInt(props.getProperty("pcrfCallingPartyNumberPosition", "4")));

        // Data CDR field positions
        config.totalFluxPosition = Integer.parseInt(props.getProperty("pcrfTotalFluxPosition", "16"));
        config.upFluxPosition = Integer.parseInt(props.getProperty("pcrfUpFluxPosition", "17"));
        config.downFluxPosition = Integer.parseInt(props.getProperty("pcrfDownFluxPosition", "18"));
        config.totalChargeFluxPosition = Integer.parseInt(props.getProperty("pcrfTotalChargeFluxPosition", "57"));

        // Account type and fee positions (for 10 accounts)
        for (int i = 1; i <= config.chargeCount; i++) {
            String accountTypePos = props.getProperty("pcrfAccountType" + i);
            String feeTypePos = props.getProperty("pcrfFeeType" + i);
            String chargeAmountPos = props.getProperty("pcrfChargeAmount" + i);
            String currentAcctAmountPos = props.getProperty("pcrfCurrentAcctAmount" + i);

            if (accountTypePos != null)
                config.getAccountTypePositions().put("pcrfAccountType" + i, Integer.parseInt(accountTypePos));
            if (feeTypePos != null) config.getFeeTypePositions().put("pcrfFeeType" + i, Integer.parseInt(feeTypePos));
            if (chargeAmountPos != null)
                config.getChargeAmountPositions().put("pcrfChargeAmount" + i, Integer.parseInt(chargeAmountPos));
            if (currentAcctAmountPos != null)
                config.getCurrentAcctAmountPositions().put("pcrfCurrentAcctAmount" + i, Integer.parseInt(currentAcctAmountPos));
        }

        return config;
    }

    private static Properties loadProperties(String fileConfig) {
        Properties props = new Properties();
        File file = new File(fileConfig);
        if (!file.exists()) {
            log.error("Configuration file not found: {}", fileConfig);
            throw new RuntimeException("Configuration file not found: " + fileConfig);
        }
        try (InputStream is = new FileInputStream(file)) {
            props.load(is);
        } catch (IOException e) {
            log.error("Failed to load {}", fileConfig, e);
            throw new RuntimeException("Failed to load configuration file: " + fileConfig, e);
        }
        return props;
    }
}
