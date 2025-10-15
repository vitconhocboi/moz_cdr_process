package com.cdr.debug;

import com.cdr.model.SystemConfig;
import com.cdr.processor.VoiceCDRProcessor;
import com.cdr.processor.DataCDRProcessor;
import com.cdr.util.ConfigUtils;
import com.cdr.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 * Standalone debug mode for processing CDR files in IntelliJ IDE
 */
public class CDRDebugMain {
    
    private static final Logger log = LoggerFactory.getLogger(CDRDebugMain.class);
    
    public static void main(String[] args) {
        System.out.println("=== CDR Processing Debug Mode ===");
        System.out.println("Available CDR types: voice, data, pcrf");

        if (args.length < 1) {
            System.err.println("Usage: java ...CDRDebugMain <cdrType>");
            System.err.println("Please provide CDR type as first argument: voice, data, or pcrf");
            return;
        }

        String cdrType = args[0].toLowerCase().trim();

        if (!cdrType.matches("voice|data|pcrf")) {
            System.err.println("Invalid CDR type. Please enter: voice, data, or pcrf");
            return;
        }

        try {
            // Load system configuration
            SystemConfig config = ConfigUtils.loadSystemConfig();
            System.out.println("Configuration loaded successfully");

            // Get input folder for the CDR type
            String inputFolder = getInputFolder(config, cdrType);
            System.out.println("Input folder: " + inputFolder);

            // Scan for files
            List<File> files = FileUtils.scanInputFolder(inputFolder);
            System.out.println("Found " + files.size() + " files in " + inputFolder);

            if (files.isEmpty()) {
                System.out.println("No files found in input folder. Please add some CDR files to process.");
                return;
            }

            // List available files
            System.out.println("\nAvailable files:");
            for (int i = 0; i < files.size(); i++) {
                System.out.println((i + 1) + ". " + files.get(i).getName());
            }

            // Automatically select the first file for processing
            File selectedFile = files.get(0);
            System.out.println("Selected file: " + selectedFile.getName());

            // Process the file
            processFile(selectedFile, config, cdrType);

        } catch (Exception e) {
            log.error("Error in debug mode", e);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String getInputFolder(SystemConfig config, String cdrType) {
        switch (cdrType) {
            case "voice":
                return config.getVoiceInputFolder();
            case "data":
                return config.getDataInputFolder();
            case "pcrf":
                return config.getPcrfInputFolder();
            default:
                throw new IllegalArgumentException("Invalid CDR type: " + cdrType);
        }
    }
    
    private static void processFile(File file, SystemConfig config, String cdrType) {
        System.out.println("\n=== Processing File ===");
        System.out.println("File: " + file.getName());
        System.out.println("Type: " + cdrType);
        System.out.println("Size: " + file.length() + " bytes");
        
        try {
            switch (cdrType) {
                case "voice":
                    VoiceCDRProcessor voiceProcessor = new VoiceCDRProcessor(file, config);
                    voiceProcessor.run();
                    break;
                case "data":
                    DataCDRProcessor dataProcessor = new DataCDRProcessor(file, config);
                    dataProcessor.run();
                    break;
            }
            
            System.out.println("✅ File processed successfully!");
            System.out.println("Check output folder for processed file");
            System.out.println("Check backup folder for original file");
            
        } catch (Exception e) {
            log.error("Error processing file: " + file.getName(), e);
            System.err.println("❌ Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
