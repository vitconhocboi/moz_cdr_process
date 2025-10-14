# CDR Processing System

A high-availability CDR (Call Detail Record) processing system designed for processing voice and data CDR files with automatic error handling and failover capabilities.

## Features

- **Master-Slave Architecture**: Parallel processing with configurable slave workers
- **High Availability**: Active-standby deployment with Pacemaker
- **Error Handling**: Automatic error detection and file movement to error folder
- **Heartbeat Monitoring**: Database-based heartbeat for failover detection
- **Java Service Wrapper**: Native system service integration
- **Comprehensive Logging**: Detailed logging with rotation

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── cdr/
│   │           ├── model/
│   │           │   ├── SystemConfig.java
│   │           │   ├── CDRRecord.java
│   │           │   ├── VoiceCDR.java
│   │           │   └── DataCDR.java
│   │           ├── processor/
│   │           │   ├── MasterController.java
│   │           │   ├── VoiceCDRProcessor.java
│   │           │   ├── DataCDRProcessor.java
│   │           │   └── HeartbeatService.java
│   │           ├── util/
│   │           │   ├── ConfigUtils.java
│   │           │   ├── FileUtils.java
│   │           │   ├── DatabaseUtils.java
│   │           │   ├── ErrorMonitor.java
│   │           │   └── ErrorAnalyzer.java
│   │           └── CDRProcessorMain.java
│   └── resources/
│       ├── application.properties
│       ├── cdr-config.properties
│       └── logback.xml
└── pom.xml
```

## Prerequisites

- Java 8 or higher
- Maven 3.6+
- MySQL 5.7+ or PostgreSQL 10+
- Linux/Unix environment for Java Service Wrapper

## Building the Application

```bash
# Clone the repository
git clone <repository-url>
cd moz_cdr_process

# Build the application
mvn clean package

# Build with production profile
mvn clean package -Pproduction
```

## Configuration

### Database Setup

Create the heartbeat table:

```sql
CREATE TABLE system_heartbeat (
    id INT PRIMARY KEY AUTO_INCREMENT,
    server_name VARCHAR(50) NOT NULL,
    last_heartbeat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'STANDBY', 'FAILED') DEFAULT 'STANDBY',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert initial records for both servers
INSERT INTO system_heartbeat (server_name, status) VALUES 
('cdr-server-01', 'STANDBY'),
('cdr-server-02', 'STANDBY');
```

### Application Configuration

Edit `src/main/resources/application.properties`:

```properties
# Folder Configuration
cdr.input.folder=/data/input
cdr.output.folder=/data/output
cdr.backup.folder=/data/backup
cdr.error.folder=/data/error

# Database Configuration
cdr.database.url=jdbc:mysql://localhost:3306/cdr_db
cdr.database.username=cdr_user
cdr.database.password=your_password

# High Availability Configuration
cdr.server.name=cdr-server-01
cdr.heartbeat.interval=30000
cdr.heartbeat.timeout=300000
```

## Deployment

### 1. Install Java Service Wrapper

```bash
# Download and install Java Service Wrapper
cd /opt
wget https://wrapper.tanukisoftware.com/download/3.5.54/wrapper-linux-x86-64-3.5.54.tar.gz
tar -xzf wrapper-linux-x86-64-3.5.54.tar.gz
mv wrapper-linux-x86-64-3.5.54 cdr-wrapper
```

### 2. Deploy Application

```bash
# Create application directory
mkdir -p /opt/cdr-processor/{bin,lib,logs,conf}

# Copy application JAR
cp target/cdr-processor.jar /opt/cdr-processor/lib/

# Copy wrapper files
cp cdr-wrapper/bin/wrapper /opt/cdr-processor/bin/
cp cdr-wrapper/lib/wrapper.jar /opt/cdr-processor/lib/
cp cdr-wrapper/conf/wrapper.conf /opt/cdr-processor/conf/
cp cdr-wrapper/conf/wrapper-license.conf /opt/cdr-processor/conf/

# Install as system service
cd /opt/cdr-processor
./bin/wrapper install
```

### 3. Start Service

```bash
# Start the service
./bin/wrapper start

# Check status
./bin/wrapper status

# View logs
tail -f logs/wrapper.log
```

## High Availability Setup

### Pacemaker Configuration

```bash
# Install Pacemaker and Corosync
yum install pacemaker corosync pcs

# Configure cluster
pcs cluster setup --name cdr-cluster cdr-server-01 cdr-server-02
pcs cluster start --all
pcs cluster enable --all

# Create resources
pcs resource create cdr-processor systemd:cdr-processor \
    op monitor interval=30s timeout=10s \
    op start timeout=60s \
    op stop timeout=60s

pcs resource create cdr-vip IPaddr2 ip=192.168.1.100 \
    op monitor interval=30s timeout=10s

# Set constraints
pcs constraint colocation add cdr-vip with cdr-processor INFINITY
pcs constraint order cdr-vip then cdr-processor
```

## Monitoring

### Error Monitoring

The system automatically moves failed files to the error folder with detailed logging:

```
/data/error/
├── voice_error_log.txt
├── data_error_log.txt
├── error_log.txt
└── failed_files/
```

### Health Checks

```bash
# Check service status
systemctl status cdr-processor

# Check cluster status
pcs status

# Check heartbeat table
mysql -u cdr_user -p -e "SELECT * FROM system_heartbeat;"
```

## File Formats

### Voice CDR Format
```
callId|callingNumber|calledNumber|chargeAmount|duration|callType|startTime|endTime|accountType1|...|accountType10
```

### Data CDR Format
```
sessionId|imsi|msisdn|totalFlux|upFlux|downFlux|duration|apn|startTime|endTime|accountType1|...|accountType10
```

## Troubleshooting

### Common Issues

1. **Service won't start**: Check Java version and wrapper configuration
2. **Database connection failed**: Verify database credentials and network connectivity
3. **Files not processing**: Check input folder permissions and file formats
4. **High error rate**: Review error logs and file formats

### Log Locations

- Application logs: `/var/log/cdr-processor/cdr-processor.log`
- Error logs: `/var/log/cdr-processor/cdr-processor-error.log`
- Wrapper logs: `/opt/cdr-processor/logs/wrapper.log`

## Performance Tuning

### JVM Settings

```bash
# Edit wrapper.conf
wrapper.java.additional.1=-Xms2g
wrapper.java.additional.2=-Xmx4g
wrapper.java.additional.3=-XX:+UseG1GC
```

### Database Tuning

```sql
-- Optimize heartbeat table
CREATE INDEX idx_heartbeat_server_status ON system_heartbeat(server_name, status);
CREATE INDEX idx_heartbeat_timestamp ON system_heartbeat(last_heartbeat);
```

## License

This project is licensed under the GPL v3 License.
