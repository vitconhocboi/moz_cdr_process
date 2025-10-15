package com.cdr.processor;

import com.cdr.model.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Heartbeat Service for High Availability monitoring
 */
public class HeartbeatService {
    
    private String serverName;
    private long heartbeatInterval;
    private long heartbeatTimeout;
    private DataSource dataSource;
    private ScheduledExecutorService scheduler;
    private static final Logger log = LoggerFactory.getLogger(HeartbeatService.class);
    
    public HeartbeatService(SystemConfig config, DataSource dataSource) {
        this.serverName = config.getServerName();
        this.heartbeatInterval = config.getHeartbeatInterval();
        this.heartbeatTimeout = config.getHeartbeatTimeout();
        this.dataSource = dataSource;
        this.scheduler = Executors.newScheduledThreadPool(2);
    }
    
    public void startHeartbeat() {
        // Send heartbeat every 30 seconds
        scheduler.scheduleWithFixedDelay(this::sendHeartbeat, 0, heartbeatInterval, TimeUnit.MILLISECONDS);
        
        // Check active server every 600 seconds
        scheduler.scheduleWithFixedDelay(this::checkActiveServer, 600_000, 600_000, TimeUnit.MILLISECONDS);
        
        log.info("Heartbeat service started for server: {}", serverName);
    }
    
    public void sendHeartbeat() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "UPDATE system_heartbeat SET last_heartbeat = NOW() " +
                        "WHERE server_name = ? AND status = 'ACTIVE'";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, serverName);
                int updated = stmt.executeUpdate();
                
                if (updated == 0) {
                    log.warn("No active heartbeat record found for server: {}", serverName);
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to send heartbeat", e);
        }
    }
    
    public void checkActiveServer() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT last_heartbeat FROM system_heartbeat " +
                        "WHERE status = 'ACTIVE' AND server_name != ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, serverName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Timestamp lastHeartbeat = rs.getTimestamp("last_heartbeat");
                        long timeDiff = System.currentTimeMillis() - lastHeartbeat.getTime();
                        
                        if (timeDiff > heartbeatTimeout) {
                            log.warn("Active server heartbeat timeout detected. Taking over...");
                            takeOverActiveRole();
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to check active server heartbeat", e);
        }
    }
    
    private void takeOverActiveRole() {
        try (Connection conn = dataSource.getConnection()) {
            String updateSql = "UPDATE system_heartbeat SET " +
                              "server_name = ?, status = 'ACTIVE', last_heartbeat = NOW() " +
                              "WHERE server_name != ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, serverName);
                stmt.setString(2, serverName);
                stmt.executeUpdate();
            }
            
            // Start CDR processing
            startCDRProcessing();
            
            log.info("Successfully took over active role for server: {}", serverName);
            
        } catch (Exception e) {
            log.error("Failed to take over active role", e);
        }
    }
    
    private void startCDRProcessing() {
        // This method would be implemented to start CDR processing
        // when this server takes over the active role
        log.info("Starting CDR processing as active server");
    }
    
    public void shutdown() {
        log.info("Shutting down heartbeat service...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
