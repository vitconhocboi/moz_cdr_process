package com.cdr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * File utility class for file operations
 */
public class FileUtils {
    
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
    
    /**
     * Get file name without extension
     */
    public static String getNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }
    
    /**
     * Get file extension
     */
    public static String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
    
    /**
     * Check if file is a Voice CDR file
     */
    public static boolean isVoiceCDR(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.contains("voice") || fileName.contains("call") || fileName.endsWith(".vcdr");
    }
    
    /**
     * Check if file is a Data CDR file
     */
    public static boolean isDataCDR(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.contains("data") || fileName.contains("session") || fileName.endsWith(".dcdr");
    }
    
    /**
     * Create directory if it doesn't exist
     */
    public static void createDirectoryIfNotExists(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created directory: {}", directoryPath);
            }
        } catch (Exception e) {
            log.error("Failed to create directory: {}", directoryPath, e);
        }
    }
    
    /**
     * Move file to error folder with timestamp and error reason
     */
    public static File moveToErrorFolder(File originalFile, String errorFolder, String errorReason) {
        try {
            createDirectoryIfNotExists(errorFolder);
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String errorFileName = String.format("%s_%s_%s", 
                getNameWithoutExtension(originalFile.getName()), 
                timestamp, 
                errorReason.replaceAll("[^a-zA-Z0-9]", "_"));
            
            File errorFile = new File(errorFolder, errorFileName + getExtension(originalFile.getName()));
            Files.move(originalFile.toPath(), errorFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            log.error("Moved file {} to error folder: {}", originalFile.getName(), errorFile.getAbsolutePath());
            return errorFile;
            
        } catch (Exception e) {
            log.error("Failed to move file {} to error folder", originalFile.getName(), e);
            return null;
        }
    }
    
    /**
     * Backup original file
     */
    public static File backupFile(File originalFile, String backupFolder) {
        try {
            createDirectoryIfNotExists(backupFolder);
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFileName = String.format("%s_%s%s", 
                getNameWithoutExtension(originalFile.getName()), 
                timestamp,
                getExtension(originalFile.getName()));
            
            File backupFile = new File(backupFolder, backupFileName);
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            log.info("Backed up file {} to: {}", originalFile.getName(), backupFile.getAbsolutePath());
            return backupFile;
            
        } catch (Exception e) {
            log.error("Failed to backup file {}", originalFile.getName(), e);
            return null;
        }
    }
    
    /**
     * Scan input folder for files
     */
    public static List<File> scanInputFolder(String inputFolder) {
        List<File> files = new ArrayList<>();
        try {
            File folder = new File(inputFolder);
            if (folder.exists() && folder.isDirectory()) {
                File[] fileArray = folder.listFiles();
                if (fileArray != null) {
                    for (File file : fileArray) {
                        if (file.isFile()) {
                            files.add(file);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to scan input folder: {}", inputFolder, e);
        }
        return files;
    }
    
    /**
     * Get file size in human readable format
     */
    public static String getFileSize(File file) {
        long bytes = file.length();
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
