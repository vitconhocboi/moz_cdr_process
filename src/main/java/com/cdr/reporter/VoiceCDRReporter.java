package com.cdr.reporter;

import com.cdr.model.SystemConfig;
import com.cdr.model.VoiceCDRRecord;
import com.cdr.util.ConfigUtils;
import com.cdr.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Voice CDR Reporter for generating detailed processing reports
 */
public class VoiceCDRReporter {
    
    private static final Logger log = LoggerFactory.getLogger(VoiceCDRReporter.class);
    private final String baseReportFolder;
    private final String reportFolder;
    private final String inputFileName;
    private final Map<String, Object> processingStats = new ConcurrentHashMap<>();
    private final List<VoiceCDRRecord> processedRecords = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong totalRecords = new AtomicLong(0);
    private final AtomicLong specialAccountRecords = new AtomicLong(0);
    private final AtomicLong totalDurationReduced = new AtomicLong(0);
    private final AtomicLong totalChargeReduced = new AtomicLong(0);
    private final AtomicLong headerWritten = new AtomicLong(0);
    private final AtomicLong fileCleaned = new AtomicLong(0);
    
    public VoiceCDRReporter(SystemConfig config, File inputFile) {
        this.baseReportFolder = ConfigUtils.getReportFolder();
        this.reportFolder = determineReportFolder(inputFile, config);
        this.inputFileName = inputFile.getName();
        createReportFolder();
    }
    
    private String determineReportFolder(File inputFile, SystemConfig config) {
        try {
            // Get relative path from voice input folder
            String relativePath = FileUtils.getRelativePath(inputFile, config.getVoiceInputFolder());
            String subfolderPath = new File(relativePath).getParent();
            
            if (subfolderPath != null && !subfolderPath.isEmpty()) {
                // Preserve subfolder structure in reports
                return baseReportFolder + "/voice" + "/" + subfolderPath;
            } else {
                // No subfolder, use base voice reports folder
                return baseReportFolder + "/voice";
            }
        } catch (Exception e) {
            log.warn("Failed to determine report folder structure, using base folder", e);
            return baseReportFolder + "/voice";
        }
    }
    
    private void createReportFolder() {
        try {
            File folder = new File(reportFolder);
            if (!folder.exists()) {
                boolean created = folder.mkdirs();
                if (created) {
                    log.info("Created report folder: {}", reportFolder);
                } else {
                    log.warn("Failed to create report folder: {}", reportFolder);
                }
            }
        } catch (Exception e) {
            log.error("Failed to create report folder: {}", reportFolder, e);
        }
    }
    
    public void recordProcessedCall(VoiceCDRRecord record) {
        processedRecords.add(record);
        totalRecords.incrementAndGet();
        
        if ("Special Account".equals(record.getProcessingType())) {
            specialAccountRecords.incrementAndGet();
        }
        
        totalDurationReduced.addAndGet(record.getDurationReduction());
        
        // Calculate total charge reduced
        double chargeReduced = 0.0;
        for (int i = 1; i <= 10; i++) {
            chargeReduced += record.getOldChargeAmount(i) - record.getNewChargeAmount(i);
        }
        totalChargeReduced.addAndGet((long) chargeReduced);
        
        // Generate detailed report for each record
        generateDetailReportForRecord(record);
        
        log.debug("Recorded processed call: {} - Duration: {} -> {}, Reduction: {} seconds",
            record.getCallingNumber(), record.getOldDuration(), record.getNewDuration(), record.getDurationReduction());
    }
    
    private double calculateReductionPercentage(VoiceCDRRecord record) {
        if (record.getOldDuration() == 0) return 0.0;
        return (double) record.getDurationReduction() / record.getOldDuration() * 100.0;
    }
    
    private double calculateAverageReduction() {
        if (totalRecords.get() == 0) return 0.0;
        return (double) totalDurationReduced.get() / totalRecords.get();
    }

    private void generateDetailReportForRecord(VoiceCDRRecord record) {
        try {
            // Ensure report folder exists before writing
            createReportFolder();
            
            File detailReportFile = new File(reportFolder, inputFileName);
            
            // Check if this is the first record to write - if so, remove previous file
            if (fileCleaned.compareAndSet(0, 1)) {
                if (detailReportFile.exists()) {
                    detailReportFile.delete();
                    log.debug("Removed previous report file: {}", detailReportFile.getAbsolutePath());
                }
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(detailReportFile, true))) {
                writeRecordDetail(writer, record);
            }
        } catch (Exception e) {
            log.error("Failed to generate detail report for record: {}", record.getCalledNumber(), e);
        }
    }
    
    private void writeRecordDetail(PrintWriter writer, VoiceCDRRecord record) {
        // Write header only once per file
        if (headerWritten.compareAndSet(0, 1)) {
            writer.println("Calling|Called|StartTime|OldDuration|NewDuration|DurationReduction|" +
                          "AcctType1|FeeType1|OldChargeAmt1|NewChargeAmt1|OldCurAcctAmt1|NewCurAcctAmt1|" +
                          "AcctType2|FeeType2|OldChargeAmt2|NewChargeAmt2|OldCurAcctAmt2|NewCurAcctAmt2|" +
                          "AcctType3|FeeType3|OldChargeAmt3|NewChargeAmt3|OldCurAcctAmt3|NewCurAcctAmt3|" +
                          "AcctType4|FeeType4|OldChargeAmt4|NewChargeAmt4|OldCurAcctAmt4|NewCurAcctAmt4|" +
                          "AcctType5|FeeType5|OldChargeAmt5|NewChargeAmt5|OldCurAcctAmt5|NewCurAcctAmt5|" +
                          "AcctType6|FeeType6|OldChargeAmt6|NewChargeAmt6|OldCurAcctAmt6|NewCurAcctAmt6|" +
                          "AcctType7|FeeType7|OldChargeAmt7|NewChargeAmt7|OldCurAcctAmt7|NewCurAcctAmt7|" +
                          "AcctType8|FeeType8|OldChargeAmt8|NewChargeAmt8|OldCurAcctAmt8|NewCurAcctAmt8|" +
                          "AcctType9|FeeType9|OldChargeAmt9|NewChargeAmt9|OldCurAcctAmt9|NewCurAcctAmt9|" +
                          "AcctType10|FeeType10|OldChargeAmt10|NewChargeAmt10|OldCurAcctAmt10|NewCurAcctAmt10|" +
                          "ProcessingType");
        }
        
        // Write the actual record data in the same format
        StringBuilder sb = new StringBuilder();
        sb.append(record.getCallingNumber()).append("|");
        sb.append(record.getCalledNumber()).append("|");
        sb.append(record.getStartTime()).append("|");
        sb.append(record.getOldDuration()).append("|");
        sb.append(record.getNewDuration()).append("|");
        sb.append(record.getDurationReduction()).append("|");
        
        // Add all 10 accounts in the exact format from design document
        for (int i = 1; i <= 10; i++) {
            sb.append(record.getAccountType(i) != null ? record.getAccountType(i) : "").append("|");
            sb.append(record.getFeeType(i) != null ? record.getFeeType(i) : "").append("|");
            sb.append(String.format("%.0f", record.getOldChargeAmount(i))).append("|");
            sb.append(String.format("%.0f", record.getNewChargeAmount(i))).append("|");
            sb.append(String.format("%.0f", record.getOldCurrentAcctAmount(i))).append("|");
            sb.append(String.format("%.0f", record.getNewCurrentAcctAmount(i))).append("|");
        }
        
        sb.append(record.getProcessingType());
        writer.println(sb.toString());
    }
}
