package com.cdr.processor;

import com.cdr.model.SystemConfig;
import com.cdr.model.DataCDR;
import com.cdr.model.DataCDRRecord;
import com.cdr.model.DataConfig;
import com.cdr.reporter.DataCDRReporter;
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
 * Data CDR Processor for processing data session records
 */
public class PCRFCDRProcessor implements Runnable {

    private File inputFile;
    private SystemConfig systemConfig;
    private DataCDRReporter reporter;
    DataConfig pcrfConfig;
    private static final Logger log = LoggerFactory.getLogger(PCRFCDRProcessor.class);

    public PCRFCDRProcessor(File inputFile, SystemConfig systemConfig) {
        this.inputFile = inputFile;
        this.systemConfig = systemConfig;
        pcrfConfig = ConfigUtils.loadPcrfConfig(systemConfig);
        this.reporter = new DataCDRReporter(systemConfig.getReportFolder(), systemConfig.getPcrfInputFolder(), inputFile);
    }

    @Override
    public void run() {
        try {
            log.info("Processing data CDR file: {} with block size: {}", inputFile.getName(), systemConfig.getBatchSize());
            processDataCDRInBlocks(inputFile);
            backupOriginalFile();

            log.info("Successfully processed data CDR file: {}", inputFile.getName());

        } catch (Exception e) {
            log.error("Error processing data CDR file: " + inputFile.getName(), e);
            moveToErrorFolder(e);
        }
    }

    private DataCDRRecord applyDataProcessingRules(DataCDR record) {
        DataCDRRecord reportRecord = new DataCDRRecord();
        reportRecord.setCallingNumber(record.getCallingNumber());
        reportRecord.setStartTime(record.getStartTime());
        reportRecord.setOldTotalFlux(record.getTotalFlux());
        reportRecord.setOldUpFlux(record.getUpFlux());
        reportRecord.setOldDownFlux(record.getDownFlux());
        reportRecord.setOldTotalChargeFlux(record.getTotalChargeFlux());

        // Store all account information
        for (int i = 1; i <= 10; i++) {
            reportRecord.setAccountType(i, record.getAccountType(i));
            reportRecord.setFeeType(i, record.getFeeType(i));
            reportRecord.setOldChargeAmount(i, record.getChargeAmount(i));
            reportRecord.setOldCurrentAcctAmount(i, record.getCurrentAcctAmount(i));
        }

        // For all account types, reduce ChargeAmount and add reduced ChargeAmount to CurrentAcctAmount
        double reductionPercentage = pcrfConfig.getReductionPercentage();
        double multiplier = (100 - reductionPercentage) / 100.0;
        for (int i = 1; i <= 10; i++) {
            String accountType = record.getAccountType(i);
            if (pcrfConfig.getExceptAccounts().contains(accountType)) {
                double oldChargeAmount = record.getChargeAmount(i);
                double oldCurrentAcctAmount = record.getCurrentAcctAmount(i);
                double reducedCharge = oldChargeAmount * multiplier;
                double reductionAmount = oldChargeAmount - reducedCharge;
                double newAcct = oldCurrentAcctAmount + reductionAmount;

                reportRecord.setNewChargeAmount(i, reducedCharge);
                reportRecord.setNewCurrentAcctAmount(i, newAcct);
            } else {
                reportRecord.setNewChargeAmount(i, record.getChargeAmount(i));
                reportRecord.setNewCurrentAcctAmount(i, record.getCurrentAcctAmount(i));
            }
        }

        // Apply standard reduction
        double newTotalFlux = record.getTotalFlux() * multiplier;
        double newUpFlux = record.getUpFlux() * multiplier;
        double newDownFlux = record.getDownFlux() * multiplier;
        double newTotalChargeFlux = record.getTotalChargeFlux() * multiplier;

        reportRecord.setNewTotalFlux(newTotalFlux);
        reportRecord.setNewUpFlux(newUpFlux);
        reportRecord.setNewDownFlux(newDownFlux);
        reportRecord.setNewTotalChargeFlux(newTotalChargeFlux);
        reportRecord.setFluxReduction(record.getTotalFlux() - newTotalFlux);

        // Update the original record with new values
        record.setTotalFlux(newTotalFlux);
        record.setUpFlux(newUpFlux);
        record.setDownFlux(newDownFlux);
        record.setTotalChargeFlux(newTotalChargeFlux);

        return reportRecord;
    }

    private void processDataCDRInBlocks(File file) throws IOException {
        int blockNumber = 0;
        int totalProcessed = 0;
        boolean isFirstBlock = true;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<DataCDR> currentBlock = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    DataCDR record = parseDataRecord(line);
                    if (record != null) {
                        currentBlock.add(record);

                        // Process block when it reaches the configured size
                        if (currentBlock.size() >= systemConfig.getBatchSize()) {
                            blockNumber++;
                            log.info("Processing block {} with {} records", blockNumber, currentBlock.size());

                            List<DataCDR> processedBlock = processBlock(currentBlock);
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

                List<DataCDR> processedBlock = processBlock(currentBlock);
                writeProcessedBlock(processedBlock, isFirstBlock);
                totalProcessed += processedBlock.size();
            }
        }

        // Finalize reporter
        reporter.finalizeReport();

        log.info("Completed processing {} blocks with {} total records", blockNumber, totalProcessed);
    }

    private List<DataCDR> processBlock(List<DataCDR> block) {
        List<DataCDR> processedBlock = new ArrayList<>();

        for (DataCDR record : block) {
            DataCDRRecord reportRecord = applyDataProcessingRules(record);
            reporter.recordProcessedSession(reportRecord);
            processedBlock.add(record);
        }

        return processedBlock;
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
            String[] fields = line.split("\\|");

            if (fields.length < 20) {
                log.warn("Invalid data CDR record format: {}", line);
                return null;
            }

            DataCDR record = new DataCDR();

            // Store original fields to preserve input structure
            record.setOriginalFields(fields.clone());

            // Use field positions from configuration
            int startTimePos = pcrfConfig.getStartTimePosition();
            int callingPartyPos = pcrfConfig.getCallingPartyNumberPosition();
            int totalFluxPos = pcrfConfig.getTotalFluxPosition();
            int upFluxPos = pcrfConfig.getUpFluxPosition();
            int downFluxPos = pcrfConfig.getDownFluxPosition();
            int totalChargeFluxPos = pcrfConfig.getTotalChargeFluxPosition();

            record.setStartTime(fields[startTimePos]);
            record.setCallingNumber(fields[callingPartyPos]);
            record.setTotalFlux(Double.parseDouble(fields[totalFluxPos]));
            record.setUpFlux(Double.parseDouble(fields[upFluxPos]));
            record.setDownFlux(Double.parseDouble(fields[downFluxPos]));
            record.setTotalChargeFlux(Double.parseDouble(fields[totalChargeFluxPos]));

            // Parse account types using configuration positions
            for (int i = 1; i <= 10; i++) {
                int accountTypePos = pcrfConfig.getAccountTypePositions().get("pcrfAccountType" + i);
                int feeTypePos = pcrfConfig.getFeeTypePositions().get("pcrfFeeType" + i);
                int chargeAmountPos = pcrfConfig.getChargeAmountPositions().get("pcrfChargeAmount" + i);
                int currentAcctAmountPos = pcrfConfig.getCurrentAcctAmountPositions().get("pcrfCurrentAcctAmount" + i);

                if (accountTypePos < fields.length) {
                    record.setAccountType(i, fields[accountTypePos]);
                }
                if (feeTypePos < fields.length) {
                    record.setFeeType(i, fields[feeTypePos]);
                }
                if (chargeAmountPos < fields.length) {
                    record.setChargeAmount(i, Double.parseDouble(fields[chargeAmountPos]));
                }
                if (currentAcctAmountPos < fields.length) {
                    record.setCurrentAcctAmount(i, Double.parseDouble(fields[currentAcctAmountPos]));
                }
            }

            return record;

        } catch (Exception e) {
            log.warn("Failed to parse data CDR record: {}", line, e);
            return null;
        }
    }

    private void writeProcessedBlock(List<DataCDR> records, boolean isFirstBlock) throws IOException {
        // Preserve subfolder structure
        String relativePath = FileUtils.getRelativePath(inputFile, systemConfig.getPcrfBackupFolder());
        String subfolderPath = new File(relativePath).getParent();

        String outputFolder = systemConfig.getPcrfOutputFolder();
        if (subfolderPath != null && !subfolderPath.isEmpty()) {
            FileUtils.createDirectoryStructure(outputFolder, subfolderPath);
            outputFolder = new File(outputFolder, subfolderPath).getAbsolutePath();
        } else {
            FileUtils.createDirectoryIfNotExists(outputFolder);
        }

        File outputFile = new File(outputFolder, inputFile.getName());

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile, !isFirstBlock))) {
            for (DataCDR record : records) {
                writer.println(formatDataRecord(record));
            }
        }

        log.info("Written {} processed data CDR records to: {}", records.size(), outputFile.getAbsolutePath());
    }

    private String formatDataRecord(DataCDR record) {
        // Store the original parsed fields to preserve input structure
        String[] fields = record.getOriginalFields();

        // Update only the fields that were modified during processing
        int totalFluxPos = pcrfConfig.getTotalFluxPosition();
        int upFluxPos = pcrfConfig.getUpFluxPosition();
        int downFluxPos = pcrfConfig.getDownFluxPosition();
        int totalChargeFluxPos = pcrfConfig.getTotalChargeFluxPosition();

        // Update flux values at their configured positions
        if (totalFluxPos < fields.length) {
            fields[totalFluxPos] = String.valueOf((long) record.getTotalFlux());
        }
        if (upFluxPos < fields.length) {
            fields[upFluxPos] = String.valueOf((long) record.getUpFlux());
        }
        if (downFluxPos < fields.length) {
            fields[downFluxPos] = String.valueOf((long) record.getDownFlux());
        }
        if (totalChargeFluxPos < fields.length) {
            fields[totalChargeFluxPos] = String.valueOf((long) record.getTotalChargeFlux());
        }

        // Update account information at configured positions
        for (int i = 1; i <= 10; i++) {
            int accountTypePos = pcrfConfig.getAccountTypePositions().get("pcrfAccountType" + i);
            int feeTypePos = pcrfConfig.getFeeTypePositions().get("pcrfFeeType" + i);
            int chargeAmountPos = pcrfConfig.getChargeAmountPositions().get("pcrfChargeAmount" + i);
            int currentAcctAmountPos = pcrfConfig.getCurrentAcctAmountPositions().get("pcrfCurrentAcctAmount" + i);

            if (accountTypePos < fields.length) {
                fields[accountTypePos] = record.getAccountType(i) != null ? record.getAccountType(i) : "0";
            }
            if (feeTypePos < fields.length) {
                fields[feeTypePos] = record.getFeeType(i) != null ? record.getFeeType(i) : "0";
            }
            if (chargeAmountPos < fields.length) {
                fields[chargeAmountPos] = String.valueOf((long) record.getChargeAmount(i));
            }
            if (currentAcctAmountPos < fields.length) {
                fields[currentAcctAmountPos] = String.valueOf((long) record.getCurrentAcctAmount(i));
            }
        }

        return String.join("|", fields);
    }

    private void backupOriginalFile() {
        // Preserve subfolder structure for backup
        String relativePath = FileUtils.getRelativePath(inputFile, systemConfig.getPcrfBackupFolder());
        String subfolderPath = new File(relativePath).getParent();

        String backupFolder = systemConfig.getPcrfBackupFolder();
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
            String relativePath = FileUtils.getRelativePath(inputFile, systemConfig.getPcrfInputFolder());
            String subfolderPath = new File(relativePath).getParent();

            String errorFolder = systemConfig.getPcrfErrorFolder();
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
            File errorLogFile = new File(systemConfig.getPcrfErrorFolder(), "data_error_log.txt");
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
