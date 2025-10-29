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
    private ExecutorService voiceExecutor;
    private ExecutorService dataExecutor;
    private ExecutorService pcrfExecutor;
    private SystemConfig systemConfig;
    private static final Logger log = LoggerFactory.getLogger(MasterController.class);

    public MasterController(SystemConfig config) {
        this.systemConfig = config;
        initialize();
    }

    public void initialize() {
        voiceExecutor = Executors.newFixedThreadPool(systemConfig.getVoiceSlaves());
        dataExecutor = Executors.newFixedThreadPool(systemConfig.getDataSlaves());
        pcrfExecutor = Executors.newFixedThreadPool(systemConfig.getPcrfSlaves());
        log.info("Master Controller initialized");
    }

    public void processFiles() {
        Runnable voiceTask = null, dataTask = null, pcrfTask = null;
        Thread voiceThread = null, dataThread = null, pcrfThread = null;

        if (systemConfig.getVoiceSlaves() > 0) {
            voiceTask = this::processVoiceFiles;
            voiceThread = new Thread(voiceTask, "VoiceFilesProcessor");
            voiceThread.start();
        }
        if (systemConfig.getDataSlaves() > 0) {
            dataTask = this::processDataFiles;
            dataThread = new Thread(dataTask, "DataFilesProcessor");
            dataThread.start();
        }
        if (systemConfig.getPcrfSlaves() > 0) {
            pcrfTask = this::processPCRFFiles;
            pcrfThread = new Thread(pcrfTask, "PCRFFilesProcessor");
            pcrfThread.start();
        }
    }

    private void processVoiceFiles() {
        List<File> files = FileUtils.scanInputFolder(systemConfig.getVoiceInputFolder());
        log.info("Found {} voice CDR files to process", files.size());

        for (File file : files) {
            try {
                voiceExecutor.submit(new VoiceCDRProcessor(file, systemConfig));
            } catch (Exception e) {
                log.error("Error processing voice file: {}", file.getName(), e);
                moveToErrorFolder(file, systemConfig.getVoiceErrorFolder(), "VOICE_PROCESSING_ERROR: " + e.getMessage());
            }
        }
    }

    private void processDataFiles() {
        List<File> files = FileUtils.scanInputFolder(systemConfig.getDataInputFolder());
        log.info("Found {} data CDR files to process", files.size());

        for (File file : files) {
            try {
                dataExecutor.submit(new DataCDRProcessor(file, systemConfig));
            } catch (Exception e) {
                log.error("Error processing data file: {}", file.getName(), e);
                moveToErrorFolder(file, systemConfig.getDataErrorFolder(), "DATA_PROCESSING_ERROR: " + e.getMessage());
            }
        }
    }

    private void processPCRFFiles() {
        List<File> files = FileUtils.scanInputFolder(systemConfig.getPcrfInputFolder());
        log.info("Found {} data CDR files to process", files.size());

        for (File file : files) {
            try {
                dataExecutor.submit(new PCRFCDRProcessor(file, systemConfig));
            } catch (Exception e) {
                log.error("Error processing data file: {}", file.getName(), e);
                moveToErrorFolder(file, systemConfig.getDataErrorFolder(), "DATA_PROCESSING_ERROR: " + e.getMessage());
            }
        }
    }

    private void moveToErrorFolder(File file, String errorFolder, String errorReason) {
        try {
            FileUtils.createDirectoryIfNotExists(errorFolder);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String errorFileName = String.format("%s_%s_%s",
                    FileUtils.getNameWithoutExtension(file.getName()),
                    timestamp,
                    errorReason.replaceAll("[^a-zA-Z0-9]", "_"));

            File errorFile = new File(errorFolder, errorFileName + FileUtils.getExtension(file.getName()));
            Files.move(file.toPath(), errorFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            log.error("Moved file {} to error folder: {}", file.getName(), errorFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to move file {} to error folder", file.getName(), e);
        }
    }

    public void shutdown() {
        log.info("Shutting down Master Controller...");
        voiceExecutor.shutdown();
        dataExecutor.shutdown();
        pcrfExecutor.shutdown();
        try {
            if (!voiceExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                voiceExecutor.shutdownNow();
            }
            if (!dataExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                dataExecutor.shutdownNow();
            }
            if (!pcrfExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                pcrfExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            voiceExecutor.shutdownNow();
            dataExecutor.shutdownNow();
            pcrfExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Master Controller shutdown completed");
    }
}
