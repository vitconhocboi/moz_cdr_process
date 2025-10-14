package com.cdr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Error analyzer for generating error reports
 */
public class ErrorAnalyzer {
    
    private static final Logger log = LoggerFactory.getLogger(ErrorAnalyzer.class);
    
    public void generateErrorReport() {
        File errorFolder = new File("/data/error");
        Map<String, Integer> errorTypes = new HashMap<>();
        Map<String, Integer> errorFiles = new HashMap<>();
        
        if (errorFolder.exists() && errorFolder.isDirectory()) {
            for (File file : errorFolder.listFiles()) {
                if (file.getName().endsWith(".txt")) {
                    String fileName = file.getName();
                    String errorType = extractErrorType(fileName);
                    errorTypes.merge(errorType, 1, Integer::sum);
                    errorFiles.merge(fileName, 1, Integer::sum);
                }
            }
        }
        
        // Generate report
        generateReport(errorTypes, errorFiles);
    }
    
    private String extractErrorType(String fileName) {
        if (fileName.contains("ParseException")) return "ParseException";
        if (fileName.contains("IOException")) return "IOException";
        if (fileName.contains("ValidationException")) return "ValidationException";
        if (fileName.contains("DatabaseException")) return "DatabaseException";
        if (fileName.contains("UNKNOWN_FILE_TYPE")) return "UnknownFileType";
        if (fileName.contains("PROCESSING_ERROR")) return "ProcessingError";
        return "Unknown";
    }
    
    private void generateReport(Map<String, Integer> errorTypes, Map<String, Integer> errorFiles) {
        log.info("Error Analysis Report:");
        log.info("Total error files: {}", errorFiles.size());
        log.info("Error types distribution: {}", errorTypes);
        
        // Could be extended to write to file or send via email
    }
}
