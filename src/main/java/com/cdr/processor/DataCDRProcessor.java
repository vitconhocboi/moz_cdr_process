package com.cdr.processor;

import com.cdr.model.SystemConfig;
import com.cdr.model.DataCDR;
import com.cdr.util.ConfigUtils;
import com.cdr.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Data CDR Processor for processing data session records
 */
public class DataCDRProcessor implements Runnable {
    
    private File inputFile;
    private SystemConfig systemConfig;
    private List<String> configuredAccounts;
    private double reductionPercentage;
    private Map<String, String> specialAccountTypes;
    private Map<String, Integer> accountTypePositions;
    private Map<String, Integer> fieldPositions;
    private static final Logger log = LoggerFactory.getLogger(DataCDRProcessor.class);
    
    public DataCDRProcessor(File inputFile, SystemConfig systemConfig) {
        this.inputFile = inputFile;
        this.systemConfig = systemConfig;
        this.configuredAccounts = ConfigUtils.getDataAccounts();
        this.reductionPercentage = ConfigUtils.getDataReductionPercentage();
        this.specialAccountTypes = ConfigUtils.getDataSpecialAccountTypes();
        this.accountTypePositions = ConfigUtils.getDataAccountTypePositions();
        this.fieldPositions = ConfigUtils.getDataFieldPositions();
    }
    
    @Override
    public void run() {
        try {
            log.info("Processing data CDR file: {}", inputFile.getName());
            List<DataCDR> records = parseDataCDR(inputFile);
            
            for (DataCDR record : records) {
                if (shouldProcessRecord(record)) {
                    applyReduction(record);
                }
            }
            
            writeProcessedFile(records);
            backupOriginalFile();
            log.info("Successfully processed data CDR file: {}", inputFile.getName());
            
        } catch (Exception e) {
            log.error("Error processing data CDR file: " + inputFile.getName(), e);
            moveToErrorFolder(e);
        }
    }
    
    private List<DataCDR> parseDataCDR(File file) throws IOException {
        List<DataCDR> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    DataCDR record = parseDataRecord(line);
                    if (record != null) {
                        records.add(record);
                    }
                }
            }
        }
        
        log.info("Parsed {} data CDR records from file: {}", records.size(), file.getName());
        return records;
    }
    
    private DataCDR parseDataRecord(String line) {
        try {
            // Assuming pipe-delimited format: sessionId|imsi|msisdn|totalFlux|upFlux|downFlux|duration|apn|startTime|endTime|accountType1|...|accountType10
            String[] fields = line.split("\\|");
            
            if (fields.length < 10) {
                log.warn("Invalid data CDR record format: {}", line);
                return null;
            }
            
            DataCDR record = new DataCDR();
            record.setSessionId(fields[0]);
            record.setImsi(fields[1]);
            record.setMsisdn(fields[2]);
            // Use field positions from configuration
            int totalFluxPos = fieldPositions.get("totalFlux");
            int upFluxPos = fieldPositions.get("upFlux");
            int downFluxPos = fieldPositions.get("downFlux");
            int totalChargeFluxPos = fieldPositions.get("totalChargeFlux");
            
            record.setTotalFlux(Double.parseDouble(fields[totalFluxPos]));
            record.setUpFlux(Double.parseDouble(fields[upFluxPos]));
            record.setDownFlux(Double.parseDouble(fields[downFluxPos]));
            record.setTotalChargeFlux(Double.parseDouble(fields[totalChargeFluxPos]));
            record.setSessionDuration(Long.parseLong(fields[6]));
            record.setApn(fields[7]);
            record.setStartTime(fields[8]);
            record.setEndTime(fields[9]);
            
            // Parse account types (fields 10-19)
            for (int i = 10; i < fields.length && i < 20; i++) {
                record.setAccountType(i - 9, fields[i]);
            }
            
            return record;
            
        } catch (Exception e) {
            log.warn("Failed to parse data CDR record: {}", line, e);
            return null;
        }
    }
    
    private boolean shouldProcessRecord(DataCDR record) {
        for (int i = 1; i <= 10; i++) {
            String accountType = record.getAccountType(i);
            if (accountType != null && configuredAccounts.contains(accountType)) {
                return true;
            }
        }
        return false;
    }
    
    private void applyReduction(DataCDR record) {
        // Check for special account types first
        for (int i = 1; i <= 5; i++) {
            String accountType = record.getAccountType(i);
            if (accountType != null && specialAccountTypes.containsKey(accountType)) {
                double discountRate = Double.parseDouble(specialAccountTypes.get(accountType));
                double multiplier = (100 - discountRate) / 100.0;
                
                record.setTotalFlux(record.getTotalFlux() * multiplier);
                record.setUpFlux(record.getUpFlux() * multiplier);
                record.setDownFlux(record.getDownFlux() * multiplier);
                record.setTotalChargeFlux(record.getTotalChargeFlux() * multiplier);
                
                log.debug("Applied special discount {}% to data CDR record: {}", discountRate, record.getSessionId());
                return;
            }
        }
        
        // Apply standard reduction
        double multiplier = (100 - reductionPercentage) / 100.0;
        
        record.setTotalFlux(record.getTotalFlux() * multiplier);
        record.setUpFlux(record.getUpFlux() * multiplier);
        record.setDownFlux(record.getDownFlux() * multiplier);
        record.setTotalChargeFlux(record.getTotalChargeFlux() * multiplier);
        
        log.debug("Applied {}% reduction to data CDR record: {}", reductionPercentage, record.getSessionId());
    }
    
    private void writeProcessedFile(List<DataCDR> records) throws IOException {
        // Preserve subfolder structure
        String relativePath = FileUtils.getRelativePath(inputFile, systemConfig.getDataInputFolder());
        String subfolderPath = new File(relativePath).getParent();
        
        String outputFolder = systemConfig.getDataOutputFolder();
        if (subfolderPath != null && !subfolderPath.isEmpty()) {
            FileUtils.createDirectoryStructure(outputFolder, subfolderPath);
            outputFolder = new File(outputFolder, subfolderPath).getAbsolutePath();
        } else {
            FileUtils.createDirectoryIfNotExists(outputFolder);
        }
        
        File outputFile = new File(outputFolder, inputFile.getName());
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (DataCDR record : records) {
                writer.println(formatDataRecord(record));
            }
        }
        
        log.info("Written {} processed data CDR records to: {}", records.size(), outputFile.getAbsolutePath());
    }
    
    private String formatDataRecord(DataCDR record) {
        return String.format("%s|%s|%s|%.2f|%.2f|%.2f|%.2f|%d|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                record.getSessionId(),
                record.getImsi(),
                record.getMsisdn(),
                record.getTotalFlux(),
                record.getUpFlux(),
                record.getDownFlux(),
                record.getTotalChargeFlux(),
                record.getSessionDuration(),
                record.getApn(),
                record.getStartTime(),
                record.getEndTime(),
                record.getAccountType1(),
                record.getAccountType2(),
                record.getAccountType3(),
                record.getAccountType4(),
                record.getAccountType5(),
                record.getAccountType6(),
                record.getAccountType7(),
                record.getAccountType8(),
                record.getAccountType9(),
                record.getAccountType10());
    }
    
    private void backupOriginalFile() {
        // Preserve subfolder structure for backup
        String relativePath = FileUtils.getRelativePath(inputFile, systemConfig.getDataInputFolder());
        String subfolderPath = new File(relativePath).getParent();
        
        String backupFolder = systemConfig.getDataBackupFolder();
        if (subfolderPath != null && !subfolderPath.isEmpty()) {
            FileUtils.createDirectoryStructure(backupFolder, subfolderPath);
            backupFolder = new File(backupFolder, subfolderPath).getAbsolutePath();
        } else {
            FileUtils.createDirectoryIfNotExists(backupFolder);
        }
        
        FileUtils.backupFile(inputFile, backupFolder);
    }
    
    private void moveToErrorFolder(Exception e) {
        try {
            // Preserve subfolder structure for error folder
            String relativePath = FileUtils.getRelativePath(inputFile, systemConfig.getDataInputFolder());
            String subfolderPath = new File(relativePath).getParent();
            
            String errorFolder = systemConfig.getDataErrorFolder();
            if (subfolderPath != null && !subfolderPath.isEmpty()) {
                FileUtils.createDirectoryStructure(errorFolder, subfolderPath);
                errorFolder = new File(errorFolder, subfolderPath).getAbsolutePath();
            } else {
                FileUtils.createDirectoryIfNotExists(errorFolder);
            }
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String errorFileName = String.format("%s_%s_DATA_ERROR_%s", 
                FileUtils.getNameWithoutExtension(inputFile.getName()), 
                timestamp,
                e.getClass().getSimpleName());
            
            File errorFile = new File(errorFolder, errorFileName + FileUtils.getExtension(inputFile.getName()));
            Files.move(inputFile.toPath(), errorFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            log.error("Moved failed data CDR file {} to error folder: {}", 
                inputFile.getName(), errorFile.getAbsolutePath());
            
            // Log detailed error information
            logDetailedError(inputFile, errorFile, e);
            
        } catch (Exception moveException) {
            log.error("Failed to move data CDR file {} to error folder", inputFile.getName(), moveException);
        }
    }
    
    private void logDetailedError(File originalFile, File errorFile, Exception e) {
        try {
            File errorLogFile = new File(systemConfig.getDataErrorFolder(), "data_error_log.txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(errorLogFile, true))) {
                writer.println(String.format("[%s] DATA CDR PROCESSING ERROR", 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
                writer.println(String.format("Original File: %s", originalFile.getAbsolutePath()));
                writer.println(String.format("Error File: %s", errorFile.getAbsolutePath()));
                writer.println(String.format("Error Type: %s", e.getClass().getSimpleName()));
                writer.println(String.format("Error Message: %s", e.getMessage()));
                writer.println("Stack Trace:");
                e.printStackTrace(writer);
                writer.println("---");
            }
        } catch (Exception logException) {
            log.error("Failed to log detailed error information", logException);
        }
    }
}
