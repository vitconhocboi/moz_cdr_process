package com.cdr.processor;

import com.cdr.model.SystemConfig;
import com.cdr.model.VoiceCDR;
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

/**
 * Voice CDR Processor for processing voice call records
 */
public class VoiceCDRProcessor implements Runnable {
    
    private File inputFile;
    private SystemConfig systemConfig;
    private List<String> configuredAccounts;
    private static final Logger log = LoggerFactory.getLogger(VoiceCDRProcessor.class);
    
    public VoiceCDRProcessor(File inputFile, SystemConfig systemConfig) {
        this.inputFile = inputFile;
        this.systemConfig = systemConfig;
        this.configuredAccounts = ConfigUtils.getVoiceAccounts();
    }
    
    @Override
    public void run() {
        try {
            log.info("Processing voice CDR file: {}", inputFile.getName());
            List<VoiceCDR> records = parseVoiceCDR(inputFile);
            
            for (VoiceCDR record : records) {
                if (shouldProcessRecord(record)) {
                    record.setChargeAmount(0.0);
                }
            }
            
            writeProcessedFile(records);
            backupOriginalFile();
            log.info("Successfully processed voice CDR file: {}", inputFile.getName());
            
        } catch (Exception e) {
            log.error("Error processing voice CDR file: " + inputFile.getName(), e);
            moveToErrorFolder(e);
        }
    }
    
    private List<VoiceCDR> parseVoiceCDR(File file) throws IOException {
        List<VoiceCDR> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    VoiceCDR record = parseVoiceRecord(line);
                    if (record != null) {
                        records.add(record);
                    }
                }
            }
        }
        
        log.info("Parsed {} voice CDR records from file: {}", records.size(), file.getName());
        return records;
    }
    
    private VoiceCDR parseVoiceRecord(String line) {
        try {
            // Assuming pipe-delimited format: callId|callingNumber|calledNumber|chargeAmount|duration|callType|startTime|endTime|accountType1|...|accountType10
            String[] fields = line.split("\\|");
            
            if (fields.length < 8) {
                log.warn("Invalid voice CDR record format: {}", line);
                return null;
            }
            
            VoiceCDR record = new VoiceCDR();
            record.setCallId(fields[0]);
            record.setCallingNumber(fields[1]);
            record.setCalledNumber(fields[2]);
            record.setChargeAmount(Double.parseDouble(fields[3]));
            record.setCallDuration(Long.parseLong(fields[4]));
            record.setCallType(fields[5]);
            record.setStartTime(fields[6]);
            record.setEndTime(fields[7]);
            
            // Parse account types (fields 8-17)
            for (int i = 8; i < fields.length && i < 18; i++) {
                record.setAccountType(i - 7, fields[i]);
            }
            
            return record;
            
        } catch (Exception e) {
            log.warn("Failed to parse voice CDR record: {}", line, e);
            return null;
        }
    }
    
    private boolean shouldProcessRecord(VoiceCDR record) {
        for (int i = 1; i <= 10; i++) {
            String accountType = record.getAccountType(i);
            if (accountType != null && configuredAccounts.contains(accountType)) {
                return true;
            }
        }
        return false;
    }
    
    private void writeProcessedFile(List<VoiceCDR> records) throws IOException {
        File outputFile = new File(systemConfig.getOutputFolder(), inputFile.getName());
        FileUtils.createDirectoryIfNotExists(systemConfig.getOutputFolder());
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (VoiceCDR record : records) {
                writer.println(formatVoiceRecord(record));
            }
        }
        
        log.info("Written {} processed voice CDR records to: {}", records.size(), outputFile.getAbsolutePath());
    }
    
    private String formatVoiceRecord(VoiceCDR record) {
        return String.format("%s|%s|%s|%.2f|%d|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                record.getCallId(),
                record.getCallingNumber(),
                record.getCalledNumber(),
                record.getChargeAmount(),
                record.getCallDuration(),
                record.getCallType(),
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
        FileUtils.backupFile(inputFile, systemConfig.getBackupFolder());
    }
    
    private void moveToErrorFolder(Exception e) {
        try {
            FileUtils.createDirectoryIfNotExists(systemConfig.getErrorFolder());
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String errorFileName = String.format("%s_%s_VOICE_ERROR_%s", 
                FileUtils.getNameWithoutExtension(inputFile.getName()), 
                timestamp,
                e.getClass().getSimpleName());
            
            File errorFile = new File(systemConfig.getErrorFolder(), errorFileName + FileUtils.getExtension(inputFile.getName()));
            Files.move(inputFile.toPath(), errorFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            log.error("Moved failed voice CDR file {} to error folder: {}", 
                inputFile.getName(), errorFile.getAbsolutePath());
            
            // Log detailed error information
            logDetailedError(inputFile, errorFile, e);
            
        } catch (Exception moveException) {
            log.error("Failed to move voice CDR file {} to error folder", inputFile.getName(), moveException);
        }
    }
    
    private void logDetailedError(File originalFile, File errorFile, Exception e) {
        try {
            File errorLogFile = new File(systemConfig.getErrorFolder(), "voice_error_log.txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(errorLogFile, true))) {
                writer.println(String.format("[%s] VOICE CDR PROCESSING ERROR", 
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
