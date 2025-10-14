package com.cdr.processor;

import com.cdr.model.SystemConfig;
import com.cdr.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Master Controller for CDR processing system
 */
public class MasterController {
    
    private String inputFolder;
    private int voiceSlaves;
    private int dataSlaves;
    private ExecutorService voiceExecutor;
    private ExecutorService dataExecutor;
    private SystemConfig systemConfig;
    private static final Logger log = LoggerFactory.getLogger(MasterController.class);
    
    public MasterController(SystemConfig config) {
        this.systemConfig = config;
        this.inputFolder = config.getInputFolder();
        this.voiceSlaves = config.getVoiceSlaves();
        this.dataSlaves = config.getDataSlaves();
        initialize();
    }
    
    public void initialize() {
        voiceExecutor = Executors.newFixedThreadPool(voiceSlaves);
        dataExecutor = Executors.newFixedThreadPool(dataSlaves);
        log.info("Master Controller initialized with {} voice slaves and {} data slaves", 
                voiceSlaves, dataSlaves);
    }
    
    public void processFiles() {
        List<File> files = FileUtils.scanInputFolder(inputFolder);
        log.info("Found {} files to process", files.size());
        
        for (File file : files) {
            try {
                if (FileUtils.isVoiceCDR(file)) {
                    voiceExecutor.submit(new VoiceCDRProcessor(file, systemConfig));
                } else if (FileUtils.isDataCDR(file)) {
                    dataExecutor.submit(new DataCDRProcessor(file, systemConfig));
                } else {
                    log.warn("Unknown file type: {}", file.getName());
                    moveToErrorFolder(file, "UNKNOWN_FILE_TYPE");
                }
            } catch (Exception e) {
                log.error("Error processing file: {}", file.getName(), e);
                moveToErrorFolder(file, "PROCESSING_ERROR: " + e.getMessage());
            }
        }
    }
    
    private void moveToErrorFolder(File file, String errorReason) {
        try {
            FileUtils.createDirectoryIfNotExists(systemConfig.getErrorFolder());
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String errorFileName = String.format("%s_%s_%s", 
                FileUtils.getNameWithoutExtension(file.getName()), 
                timestamp, 
                errorReason.replaceAll("[^a-zA-Z0-9]", "_"));
            
            File errorFile = new File(systemConfig.getErrorFolder(), errorFileName + FileUtils.getExtension(file.getName()));
            Files.move(file.toPath(), errorFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            log.error("Moved file {} to error folder: {}", file.getName(), errorFile.getAbsolutePath());
            
            // Log error details to error log file
            logErrorDetails(file, errorReason, errorFile);
            
        } catch (Exception e) {
            log.error("Failed to move file {} to error folder", file.getName(), e);
        }
    }
    
    private void logErrorDetails(File originalFile, String errorReason, File errorFile) {
        try {
            File errorLogFile = new File(systemConfig.getErrorFolder(), "error_log.txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(errorLogFile, true))) {
                writer.println(String.format("[%s] ERROR: %s", 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    errorReason));
                writer.println(String.format("Original File: %s", originalFile.getAbsolutePath()));
                writer.println(String.format("Error File: %s", errorFile.getAbsolutePath()));
                writer.println("---");
            }
        } catch (Exception e) {
            log.error("Failed to log error details", e);
        }
    }
    
    public void shutdown() {
        log.info("Shutting down Master Controller...");
        voiceExecutor.shutdown();
        dataExecutor.shutdown();
        try {
            if (!voiceExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                voiceExecutor.shutdownNow();
            }
            if (!dataExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                dataExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
