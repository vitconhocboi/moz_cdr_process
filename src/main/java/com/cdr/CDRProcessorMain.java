package com.cdr;

import com.cdr.model.SystemConfig;
import com.cdr.processor.MasterController;
import com.cdr.util.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main application class for CDR Processing System
 */
public class CDRProcessorMain {
    
    private static final Logger log = LoggerFactory.getLogger(CDRProcessorMain.class);
    private MasterController masterController;
    private SystemConfig systemConfig;
    private volatile boolean running = true;
    
    public static void main(String[] args) {
        // Check for help argument
        if (args.length > 0 && ("--help".equals(args[0]) || "-h".equals(args[0]))) {
            printUsage();
            System.exit(0);
        }
        
        // Get configuration file path from arguments
        String configFile = args.length > 0 ? args[0] : "application.properties";
        
        CDRProcessorMain app = new CDRProcessorMain();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown signal received...");
            app.shutdown();
        }));
        
        try {
            app.start(configFile);
        } catch (Exception e) {
            log.error("Failed to start CDR Processor", e);
            System.exit(1);
        }
    }
    
    public void start(String configFile) throws Exception {
        log.info("Starting CDR Processor Application with config file: {}", configFile);
        
        // Load configuration
        systemConfig = ConfigUtils.loadSystemConfig(configFile);
        log.info("Configuration loaded successfully from: {}", configFile);
        
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
        
        log.info("CDR Processor Application shutdown completed");
    }
    
    private static void printUsage() {
        System.out.println("CDR Processor Application");
        System.out.println("Usage: java -jar cdr-processor.jar [config-file] [options]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  config-file    Path to the application.properties file (default: application.properties)");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help     Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar cdr-processor.jar");
        System.out.println("  java -jar cdr-processor.jar /path/to/config.properties");
        System.out.println("  java -jar cdr-processor.jar --help");
    }
}
