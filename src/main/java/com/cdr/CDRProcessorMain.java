package com.cdr;

import com.cdr.model.SystemConfig;
import com.cdr.processor.HeartbeatService;
import com.cdr.processor.MasterController;
import com.cdr.util.ConfigUtils;
import com.cdr.util.DatabaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * Main application class for CDR Processing System
 */
public class CDRProcessorMain {
    
    private static final Logger log = LoggerFactory.getLogger(CDRProcessorMain.class);
    private MasterController masterController;
    private HeartbeatService heartbeatService;
    private SystemConfig systemConfig;
    private DataSource dataSource;
    private volatile boolean running = true;
    
    public static void main(String[] args) {
        CDRProcessorMain app = new CDRProcessorMain();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown signal received...");
            app.shutdown();
        }));
        
        try {
            app.start();
        } catch (Exception e) {
            log.error("Failed to start CDR Processor", e);
            System.exit(1);
        }
    }
    
    public void start() throws Exception {
        log.info("Starting CDR Processor Application...");
        
        // Load configuration
        systemConfig = ConfigUtils.loadSystemConfig();
        log.info("Configuration loaded successfully");
        
        // Initialize database connection
        dataSource = DatabaseUtils.createDataSource(systemConfig);
        log.info("Database connection established");
        
        // Initialize heartbeat service
        heartbeatService = new HeartbeatService(systemConfig, dataSource);
        heartbeatService.startHeartbeat();
        log.info("Heartbeat service started");
        
        // Initialize master controller
        masterController = new MasterController(systemConfig);
        log.info("Master controller initialized");
        
        // Start processing
        startProcessing();
        
        log.info("CDR Processor Application started successfully");
    }
    
    private void startProcessing() {
        new Thread(() -> {
            while (running) {
                try {
                    masterController.processFiles();
                    Thread.sleep(systemConfig.getProcessingInterval());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error during processing", e);
                }
            }
        }).start();
    }
    
    public void shutdown() {
        log.info("Shutting down CDR Processor Application...");
        running = false;
        
        if (masterController != null) {
            masterController.shutdown();
        }
        
        if (heartbeatService != null) {
            heartbeatService.shutdown();
        }
        
        log.info("CDR Processor Application shutdown completed");
    }
}
