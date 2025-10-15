package com.cdr.processor;

import com.cdr.model.SystemConfig;
import com.cdr.model.VoiceCDR;
import com.cdr.model.VoiceCDRRecord;
import com.cdr.model.VoiceConfig;
import com.cdr.reporter.VoiceCDRReporter;
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
import java.util.Set;

/**
 * Voice CDR Processor for processing voice call records with new design
 */
public class VoiceCDRProcessor implements Runnable {
    
    private File inputFile;
    private SystemConfig systemConfig;
    VoiceConfig voiceConfig;
    private VoiceCDRReporter reporter;
    private static final Logger log = LoggerFactory.getLogger(VoiceCDRProcessor.class);
    
    public VoiceCDRProcessor(File inputFile, SystemConfig systemConfig) {
        this.inputFile = inputFile;
        this.systemConfig = systemConfig;

        voiceConfig = ConfigUtils.loadVoiceConfig();
        this.reporter = new VoiceCDRReporter(systemConfig, inputFile);
    }
    
    @Override
    public void run() {
        try {
            log.info("Processing voice CDR file: {} with block size: {}", inputFile.getName(), systemConfig.getBatchSize());
            processVoiceCDRInBlocks(inputFile);
            backupOriginalFile();
            
            log.info("Successfully processed voice CDR file: {}", inputFile.getName());
            
        } catch (Exception e) {
            log.error("Error processing voice CDR file: " + inputFile.getName(), e);
            moveToErrorFolder(e);
        }
    }
    
    private VoiceCDRRecord applyVoiceProcessingRules(VoiceCDR record) {
        VoiceCDRRecord reportRecord = new VoiceCDRRecord();
        reportRecord.setCallingNumber(record.getCallingNumber());
        reportRecord.setCalledNumber(record.getCalledNumber());
        reportRecord.setStartTime(record.getStartTime());
        reportRecord.setOldDuration(record.getCallDuration());
        
        // Store all account information
        for (int i = 1; i <= 10; i++) {
            reportRecord.setAccountType(i, record.getAccountType(i));
            reportRecord.setFeeType(i, record.getFeeType(i));
            reportRecord.setOldChargeAmount(i, record.getChargeAmount(i));
            reportRecord.setOldCurrentAcctAmount(i, record.getCurrentAcctAmount(i));
        }
        // Check for special account types first
        for (int i = 1; i <= 10; i++) {
            String accountType = record.getAccountType(i);
            if (accountType != null && voiceConfig.getSpecialAccountTypes().contains(accountType)) {
                String feeType = record.getFeeType(i);
                double chargeAmount = record.getChargeAmount(i);
                double currentAcctAmount = record.getCurrentAcctAmount(i);
                
                // Get field position names for this account
                String chargeAmountPos = "ChargeAmount" + i;
                String currentAcctAmountPos = "CurrentAcctAmount" + i;
                
                // Calculate duration reduction based on fee type
                long durationReduction = 0;
                if (feeType != null && !feeType.equals(voiceConfig.getFeeTypeMoneyValue())) {
                    // If fee type is not money value (e.g., 0 for data), use charge amount directly
                    durationReduction = (long) chargeAmount;
                } else {
                    // If fee type is money value, calculate using rate
                    durationReduction = (long) (chargeAmount / voiceConfig.getAmountRate());
                }
                
                // Reduce call duration
                long currentDuration = record.getCallDuration();
                long newDuration = Math.max(0, currentDuration - durationReduction);
                record.setCallDuration(newDuration);
                // Update original field for call duration
                record.setOriginalField(voiceConfig.getCallDurationPosition(), String.valueOf(newDuration));
                
                // Set charge amount to 0 for special accounts
                record.setChargeAmount(i, 0.0);
                // Update original field for charge amount
                if (voiceConfig.getAccountTypePositions().containsKey(chargeAmountPos)) {
                    int pos = voiceConfig.getAccountTypePositions().get(chargeAmountPos);
                    record.setOriginalField(pos, "0");
                }
                
                // Add the old charge amount to current account amount
                record.setCurrentAcctAmount(i, currentAcctAmount + chargeAmount);
                // Update original field for current account amount
                if (voiceConfig.getAccountTypePositions().containsKey(currentAcctAmountPos)) {
                    int pos = voiceConfig.getAccountTypePositions().get(currentAcctAmountPos);
                    record.setOriginalField(pos, String.format("%.0f", currentAcctAmount + chargeAmount));
                }
                
                // Update report record
                reportRecord.setNewDuration(newDuration);
                reportRecord.setDurationReduction(durationReduction);
                reportRecord.setAccountType(accountType);
                reportRecord.setProcessingType("Special Account");
                reportRecord.setNewChargeAmount(i, 0.0);
                reportRecord.setNewCurrentAcctAmount(i, currentAcctAmount + chargeAmount);
                
                log.debug("Applied special account processing for account {} in record: {} - " +
                         "Duration: {} -> {}, ChargeAmount: {} -> 0, CurrentAcctAmount: {} -> {}",
                    accountType, record.getCallingNumber(), currentDuration, newDuration,
                    chargeAmount, currentAcctAmount, record.getCurrentAcctAmount(i));
            }
        }

        return reportRecord;
    }
    
    private void processVoiceCDRInBlocks(File file) throws IOException {
        int blockNumber = 0;
        int totalProcessed = 0;
        boolean isFirstBlock = true;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<VoiceCDR> currentBlock = new ArrayList<>();
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    VoiceCDR record = parseVoiceRecord(line);
                    if (record != null) {
                        currentBlock.add(record);
                        
                        // Process block when it reaches the configured size
                        if (currentBlock.size() >= systemConfig.getBatchSize()) {
                            blockNumber++;
                            log.info("Processing block {} with {} records", blockNumber, currentBlock.size());
                            
                            List<VoiceCDR> processedBlock = processBlock(currentBlock);
                            writeProcessedBlock(processedBlock, isFirstBlock);
                            totalProcessed += processedBlock.size();
                            isFirstBlock = false;
                            
                            // Clear current block to free memory
                            currentBlock.clear();
                            
                            // Force garbage collection to free memory
                            System.gc();
                        }
                    }
                }
            }
            
            // Process remaining records in the last block
            if (!currentBlock.isEmpty()) {
                blockNumber++;
                log.info("Processing final block {} with {} records", blockNumber, currentBlock.size());
                
                List<VoiceCDR> processedBlock = processBlock(currentBlock);
                writeProcessedBlock(processedBlock, isFirstBlock);
                totalProcessed += processedBlock.size();
            }
        }
        
        log.info("Completed processing {} blocks with {} total records", blockNumber, totalProcessed);
    }
    
    private List<VoiceCDR> processBlock(List<VoiceCDR> block) {
        List<VoiceCDR> processedBlock = new ArrayList<>();
        
        for (VoiceCDR record : block) {
            if (shouldProcessRecord(record)) {
                VoiceCDRRecord reportRecord = applyVoiceProcessingRules(record);
                reporter.recordProcessedCall(reportRecord);
            }
            processedBlock.add(record);
        }
        
        return processedBlock;
    }
    
    private VoiceCDR parseVoiceRecord(String line) {
        try {
            // Parse pipe-delimited format based on MobilePostpaid_CDR description
            String[] fields = line.split("\\|");
            
            if (fields.length < 115) { // Need at least 115 fields for all account types
                log.warn("Invalid voice CDR record format - insufficient fields: {}", line);
                return null;
            }
            
            VoiceCDR record = new VoiceCDR();
            
            // Store all original fields to preserve complete structure
            record.setOriginalFields(fields.clone());
            
            // Basic fields
            record.setStartTime(fields[voiceConfig.getStartTimePosition()]); // TimeStamp
            record.setCallingNumber(fields[voiceConfig.getCallingPartyNumberPosition()]); // CallingPartyNumber
            record.setCalledNumber(fields[voiceConfig.getCalledPartyNumberPosition()]); // CalledPartyNumber (assuming same as calling for voice)
            record.setCallDuration(Long.parseLong(fields[voiceConfig.getCallDurationPosition()])); // CallDuration at position 22
            
            // Parse account information for 10 accounts
            for (int i = 1; i <= 10; i++) {
                String accountTypePos = "AccountType" + i;
                String feeTypePos = "FeeType" + i;
                String chargeAmountPos = "ChargeAmount" + i;
                String currentAcctAmountPos = "CurrentAcctAmount" + i;
                
                if (voiceConfig.getAccountTypePositions().containsKey(accountTypePos)) {
                    int pos = voiceConfig.getAccountTypePositions().get(accountTypePos);
                    if (pos < fields.length) {
                        record.setAccountType(i, fields[pos]);
                    }
                }
                
                if (voiceConfig.getFeeTypePositions().containsKey(feeTypePos)) {
                    int pos = voiceConfig.getFeeTypePositions().get(feeTypePos);
                    if (pos < fields.length) {
                        record.setFeeType(i, fields[pos]);
                    }
                }
                
                if (voiceConfig.getChargeAmountPositions().containsKey(chargeAmountPos)) {
                    int pos = voiceConfig.getChargeAmountPositions().get(chargeAmountPos);
                    if (pos < fields.length) {
                        try {
                            record.setChargeAmount(i, Double.parseDouble(fields[pos]));
                        } catch (NumberFormatException e) {
                            record.setChargeAmount(i, 0.0);
                        }
                    }
                }
                
                if (voiceConfig.getCurrentAcctAmountPositions().containsKey(currentAcctAmountPos)) {
                    int pos = voiceConfig.getCurrentAcctAmountPositions().get(currentAcctAmountPos);
                    if (pos < fields.length) {
                        try {
                            record.setCurrentAcctAmount(i, Double.parseDouble(fields[pos]));
                        } catch (NumberFormatException e) {
                            record.setCurrentAcctAmount(i, 0.0);
                        }
                    }
                }
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
            if (accountType != null && voiceConfig.getSpecialAccountTypes().contains(accountType)) {
                return true;
            }
        }
        return false;
    }
    
    private void writeProcessedBlock(List<VoiceCDR> records, boolean isFirstBlock) throws IOException {
        // Preserve subfolder structure
        String relativePath = FileUtils.getRelativePath(inputFile, systemConfig.getVoiceInputFolder());
        String subfolderPath = new File(relativePath).getParent();
        
        String outputFolder = systemConfig.getVoiceOutputFolder();
        if (subfolderPath != null && !subfolderPath.isEmpty()) {
            FileUtils.createDirectoryStructure(outputFolder, subfolderPath);
            outputFolder = new File(outputFolder, subfolderPath).getAbsolutePath();
        } else {
            FileUtils.createDirectoryIfNotExists(outputFolder);
        }
        
        File outputFile = new File(outputFolder, inputFile.getName());
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile, !isFirstBlock))) {
            for (VoiceCDR record : records) {
                writer.println(formatVoiceRecord(record));
            }
        }
        
        log.info("Written {} processed voice CDR records to: {}", records.size(), outputFile.getAbsolutePath());
    }
    
    private String formatVoiceRecord(VoiceCDR record) {
        // Output the complete original structure with only modified values
        String[] fields = record.getOriginalFields();
        // Join all fields with pipe delimiter, preserving original structure
        return String.join("|", fields);
    }
    
    private void backupOriginalFile() {
        // Preserve subfolder structure for backup
        String relativePath = FileUtils.getRelativePath(inputFile, systemConfig.getVoiceInputFolder());
        String subfolderPath = new File(relativePath).getParent();
        
        String backupFolder = systemConfig.getVoiceBackupFolder();
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
            String relativePath = FileUtils.getRelativePath(inputFile, systemConfig.getVoiceInputFolder());
            String subfolderPath = new File(relativePath).getParent();
            
            String errorFolder = systemConfig.getVoiceErrorFolder();
            if (subfolderPath != null && !subfolderPath.isEmpty()) {
                FileUtils.createDirectoryStructure(errorFolder, subfolderPath);
                errorFolder = new File(errorFolder, subfolderPath).getAbsolutePath();
            } else {
                FileUtils.createDirectoryIfNotExists(errorFolder);
            }
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String errorFileName = String.format("%s_%s_VOICE_ERROR_%s", 
                FileUtils.getNameWithoutExtension(inputFile.getName()), 
                timestamp,
                e.getClass().getSimpleName());
            
            File errorFile = new File(errorFolder, errorFileName + FileUtils.getExtension(inputFile.getName()));
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
            File errorLogFile = new File(systemConfig.getVoiceErrorFolder(), "voice_error_log.txt");
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
