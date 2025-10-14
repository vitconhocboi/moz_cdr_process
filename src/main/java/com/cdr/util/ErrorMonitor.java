package com.cdr.util;

import com.cdr.model.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Error monitoring utility for tracking and analyzing errors
 */
public class ErrorMonitor {
    
    private static final Logger log = LoggerFactory.getLogger(ErrorMonitor.class);
    private final File errorFolder;
    private final AtomicLong errorCount = new AtomicLong(0);
    private final Map<String, AtomicLong> errorTypes = new ConcurrentHashMap<>();
    
    public ErrorMonitor(SystemConfig config) {
        this.errorFolder = new File(config.getErrorFolder());
    }
    
    public void recordError(String errorType, String fileName) {
        errorCount.incrementAndGet();
        errorTypes.computeIfAbsent(errorType, k -> new AtomicLong(0)).incrementAndGet();
        
        log.error("Error recorded - Type: {}, File: {}, Total Errors: {}", 
            errorType, fileName, errorCount.get());
        
        // Alert if error rate is too high
        if (errorCount.get() > 100) {
            sendAlert("High error rate detected: " + errorCount.get() + " errors");
        }
    }
    
    public Map<String, Object> getErrorStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalErrors", errorCount.get());
        stats.put("errorTypes", new HashMap<>(errorTypes));
        stats.put("errorFolderSize", getErrorFolderSize());
        return stats;
    }
    
    private long getErrorFolderSize() {
        if (!errorFolder.exists()) {
            return 0;
        }
        return Arrays.stream(errorFolder.listFiles())
            .mapToLong(File::length)
            .sum();
    }
    
    private void sendAlert(String message) {
        // Implementation for sending alerts (email, SMS, etc.)
        log.warn("ALERT: {}", message);
    }
}
