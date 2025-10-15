package com.cdr.reporter;

import com.cdr.model.SystemConfig;
import com.cdr.model.DataCDRRecord;
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
 * Data CDR Reporter for generating detailed processing reports
 */
public class DataCDRReporter {
    
    private static final Logger log = LoggerFactory.getLogger(DataCDRReporter.class);
    private final String baseReportFolder;
    private final String reportFolder;
    private final String inputFileName;
    private final List<DataCDRRecord> processedRecords = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong totalRecords = new AtomicLong(0);
    private final AtomicLong headerWritten = new AtomicLong(0);
    private final AtomicLong fileCleaned = new AtomicLong(0);
    
    public DataCDRReporter(SystemConfig config, File inputFile) {
        this.baseReportFolder = ConfigUtils.getReportFolder();
        this.reportFolder = determineReportFolder(inputFile, config);
        this.inputFileName = inputFile.getName();
        createReportFolder();
    }
    
    private String determineReportFolder(File inputFile, SystemConfig config) {
        String relativePath = FileUtils.getRelativePath(inputFile, config.getDataInputFolder());
        String subfolderPath = new File(relativePath).getParent();
        
        if (subfolderPath != null && !subfolderPath.isEmpty()) {
            return new File(baseReportFolder, "data" + File.separator + subfolderPath).getAbsolutePath();
        } else {
            return new File(baseReportFolder, "data").getAbsolutePath();
        }
    }
    
    private void createReportFolder() {
        FileUtils.createDirectoryIfNotExists(reportFolder);
        log.info("Data CDR report folder created: {}", reportFolder);
    }
    
    public void recordProcessedSession(DataCDRRecord record) {
        processedRecords.add(record);
        totalRecords.incrementAndGet();
        
        // Write header once
        if (headerWritten.compareAndSet(0, 1)) {
            writeReportHeader();
        }
        
        // Write record to report
        writeRecordToReport(record);
        
        // Clean up old records periodically
        if (processedRecords.size() % 1000 == 0) {
            cleanOldRecords();
        }
    }
    
    private void writeReportHeader() {
        try {
            File reportFile = new File(reportFolder, inputFileName);
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile, false))) {
                writer.println("CallingNumber,StartTime,OldTotalFlux,NewTotalFlux,FluxReduction," +
                    "OldUpFlux,NewUpFlux,OldDownFlux,NewDownFlux,OldTotalChargeFlux,NewTotalChargeFlux," +
                    "dataAccountType1,dataFeeType1,dataChargeAmount1_New,dataChargeAmount1_Old,dataCurrentAcctAmount1_New,dataCurrentAcctAmount1_Old," +
                    "dataAccountType2,dataFeeType2,dataChargeAmount2_New,dataChargeAmount2_Old,dataCurrentAcctAmount2_New,dataCurrentAcctAmount2_Old," +
                    "dataAccountType3,dataFeeType3,dataChargeAmount3_New,dataChargeAmount3_Old,dataCurrentAcctAmount3_New,dataCurrentAcctAmount3_Old," +
                    "dataAccountType4,dataFeeType4,dataChargeAmount4_New,dataChargeAmount4_Old,dataCurrentAcctAmount4_New,dataCurrentAcctAmount4_Old," +
                    "dataAccountType5,dataFeeType5,dataChargeAmount5_New,dataChargeAmount5_Old,dataCurrentAcctAmount5_New,dataCurrentAcctAmount5_Old," +
                    "dataAccountType6,dataFeeType6,dataChargeAmount6_New,dataChargeAmount6_Old,dataCurrentAcctAmount6_New,dataCurrentAcctAmount6_Old," +
                    "dataAccountType7,dataFeeType7,dataChargeAmount7_New,dataChargeAmount7_Old,dataCurrentAcctAmount7_New,dataCurrentAcctAmount7_Old," +
                    "dataAccountType8,dataFeeType8,dataChargeAmount8_New,dataChargeAmount8_Old,dataCurrentAcctAmount8_New,dataCurrentAcctAmount8_Old," +
                    "dataAccountType9,dataFeeType9,dataChargeAmount9_New,dataChargeAmount9_Old,dataCurrentAcctAmount9_New,dataCurrentAcctAmount9_Old," +
                    "dataAccountType10,dataFeeType10,dataChargeAmount10_New,dataChargeAmount10_Old,dataCurrentAcctAmount10_New,dataCurrentAcctAmount10_Old");
            }
            
            log.info("Data CDR report header written to: {}", reportFile.getAbsolutePath());
            
        } catch (Exception e) {
            log.error("Error writing data CDR report header", e);
        }
    }
    
    private void writeRecordToReport(DataCDRRecord record) {
        try {
            File reportFile = new File(reportFolder, inputFileName);
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile, true))) {
                writer.printf("%s,%s,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,",
                    record.getCallingNumber(),
                    record.getStartTime(),
                    record.getOldTotalFlux(),
                    record.getNewTotalFlux(),
                    record.getFluxReduction(),
                    record.getOldUpFlux(),
                    record.getNewUpFlux(),
                    record.getOldDownFlux(),
                    record.getNewDownFlux(),
                    record.getOldTotalChargeFlux(),
                    record.getNewTotalChargeFlux()
                );
                
                // Group by index 1-10: AccountType, FeeType, ChargeAmount (New, Old), CurrentAcctAmount (New, Old)
                for (int i = 1; i <= 10; i++) {
                    writer.printf("%s,%s,%.0f,%.0f,%.0f,%.0f,", 
                        record.getAccountType(i) != null ? record.getAccountType(i) : "",
                        record.getFeeType(i) != null ? record.getFeeType(i) : "",
                        record.getNewChargeAmount(i),
                        record.getOldChargeAmount(i),
                        record.getNewCurrentAcctAmount(i),
                        record.getOldCurrentAcctAmount(i));
                }
                
                writer.println();
            }
            
        } catch (Exception e) {
            log.error("Error writing data CDR record to report", e);
        }
    }
    
    private void cleanOldRecords() {
        if (fileCleaned.compareAndSet(0, 1)) {
            // Keep only the last 10000 records in memory
            if (processedRecords.size() > 10000) {
                synchronized (processedRecords) {
                    int toRemove = processedRecords.size() - 10000;
                    for (int i = 0; i < toRemove; i++) {
                        processedRecords.remove(0);
                    }
                }
            }
            fileCleaned.set(0);
        }
    }
    
    
    public void finalizeReport() {
        log.info("Data CDR report finalized for file: {}", inputFileName);
    }
}
