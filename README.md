# EventMonitor

A real-time event tracking system built with microservices architecture, featuring Kafka message streaming, Prometheus metrics collection, and React frontend. This system demonstrates how user interactions can be tracked, processed, and monitored through a complete data pipeline.

## 🏗️ Architecture Overview

```
Frontend (React) → ProducerService (Spring Boot) → Kafka → ConsumerService (Spring Boot) → Prometheus → Grafana
```

- **Frontend**: React application that captures user events and sends them to the backend
- **ProducerService**: Spring Boot service that receives events and publishes them to Kafka (Port: 8080)
- **ConsumerService**: Spring Boot service that consumes events from Kafka and exposes Prometheus metrics (Port: 8081)
- **Kafka**: Message broker for event streaming (Default: Port 9092)
- **Prometheus**: Metrics collection and storage (Port: 8082)
- **Grafana**: Visualization and dashboards for monitoring

## 🛠️ Tech Stack

### Frontend
![image](https://github.com/user-attachments/assets/79ee17c4-fa52-4550-bf28-ff3b65ec29b3)
- **React 18.2.0**
- **JavaScript/CSS**
- **Create React App**

### Backend Services

- **Java 21** (Temurin JDK)
- **Spring Boot 3.2.5**
- **Spring Kafka**
- **Gradle 8.14.2**
- **Lombok**

### Infrastructure

- **Apache Kafka** (Message Streaming)
- **Prometheus** (Metrics Collection)
- **Grafana** (Visualization)
- **Ubuntu VM** (Recommended for Kafka/Prometheus/Grafana)

## 📋 Prerequisites

### Local Development

- **Java 21** (Temurin JDK recommended)
- **Node.js** (16+ recommended)
- **npm** or **yarn**

### Infrastructure Setup

Choose one of the following deployment options:

#### Option 1: Ubuntu VM (Recommended)

- Ubuntu 20.04+ VM
- Docker and Docker Compose (optional)
- Minimum 4GB RAM, 2 CPU cores

#### Option 2: AWS EC2

- EC2 instance (t3.medium or larger)
- Security groups configured for ports: 9092 (Kafka), 9090 (Prometheus), 3000 (Grafana)

#### Option 3: Local Setup

- Apache Kafka installed locally
![image](https://github.com/user-attachments/assets/7a85966f-c440-4963-b437-c1b680f9f278)

- Prometheus installed locally
![image](https://github.com/user-attachments/assets/0007cc25-c1e6-46ee-a322-cc4673543452)

- Grafana installed locally

## 🚀 Quick Start

### 1. Infrastructure Setup

#### Ubuntu VM Setup (Recommended)

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 21
sudo apt install openjdk-21-jdk -y

# Install Docker (optional, for easy Kafka/Prometheus/Grafana setup)
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo apt install docker-compose -y
```

#### Kafka Setup

```bash
# Download Kafka
wget https://downloads.apache.org/kafka/2.13-3.6.0/kafka_2.13-3.6.0.tgz
tar -xzf kafka_2.13-3.6.0.tgz
cd kafka_2.13-3.6.0

# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka Server (in a new terminal)
bin/kafka-server-start.sh config/server.properties

# Create topic
bin/kafka-topics.sh --create --topic tracking-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

#### Prometheus Setup

```bash
# Download Prometheus
wget https://github.com/prometheus/prometheus/releases/download/v2.45.0/prometheus-2.45.0.linux-amd64.tar.gz
tar -xzf prometheus-2.45.0.linux-amd64.tar.gz
cd prometheus-2.45.0.linux-amd64

# Create prometheus.yml configuration
cat > prometheus.yml << EOF
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'consumer-service'
    static_configs:
      - targets: ['localhost:8082']
EOF

# Start Prometheus
./prometheus --config.file=prometheus.yml --storage.tsdb.path=./data
```

#### Grafana Setup

```bash
# Install Grafana
sudo apt-get install -y adduser libfontconfig1
wget https://dl.grafana.com/oss/release/grafana_10.2.0_amd64.deb
sudo dpkg -i grafana_10.2.0_amd64.deb

# Start Grafana
sudo systemctl start grafana-server
sudo systemctl enable grafana-server

# Access Grafana at http://your-vm-ip:3000 (admin/admin)
```

### 2. Application Configuration

Update the Kafka broker addresses in both services:

**ProducerService Configuration**:

```properties
# ProducerService/app/src/main/resources/application.properties
spring.kafka.bootstrap-servers=YOUR_VM_IP:9092
```

**ConsumerService Configuration**:

```properties
# ConsumerService/app/src/main/resources/application.properties
spring.kafka.bootstrap-servers=YOUR_VM_IP:9092
```

**Frontend Configuration**:

```javascript
// Frontend/src/LandingPage.js
// Update the API endpoint if running on different server
await fetch("http://YOUR_VM_IP:8080/producer/event", {
```

### 3. Running the Services

#### Start Backend Services

**ProducerService**:

```bash
cd ProducerService/
./gradlew bootRun
# Service will start on http://localhost:8080
```

**ConsumerService**:

```bash
cd ConsumerService/
./gradlew bootRun
# Service will start on http://localhost:8081
# Prometheus metrics available at http://localhost:8082/metrics
```

#### Start Frontend

```bash
cd Frontend/
npm install
npm start
# Frontend will start on http://localhost:3000
```

## 📊 Event Flow

1. **User Interaction**: User clicks buttons on the React frontend
2. **Event Capture**: Frontend sends POST request to ProducerService
3. **Kafka Publishing**: ProducerService publishes event to `tracking-events` topic
4. **Event Consumption**: ConsumerService consumes events from Kafka
5. **Metrics Update**: ConsumerService increments Prometheus counters
6. **Metrics Collection**: Prometheus scrapes metrics from ConsumerService
7. **Visualization**: Grafana displays real-time dashboards

## 🎯 Monitored Events

The system tracks two main events:

### User Click Events

- **Event**: `userClick`
- **Metric**: `start_learning_counter`
- **Description**: Tracks "Start Learning" button clicks

### Explore Click Events

- **Event**: `exploreClick`
- **Metric**: `explore_courses_counter`
- **Description**: Tracks "Explore Courses" button clicks

## 🔧 API Endpoints

### ProducerService (Port 8080)

```http
POST /producer/event
Content-Type: application/json

{
  "event": "userClick" | "exploreClick"
}
```

### ConsumerService (Port 8081)

```http
GET /actuator/health
GET /actuator/metrics
```

### Prometheus Metrics (Port 8082)

```http
GET /metrics
```

## 📈 Grafana Dashboard Setup

1. Access Grafana: `http://your-vm-ip:3000`
2. Login: `admin/admin`
3. Add Prometheus data source: `http://localhost:9090`
4. Create dashboard with panels for:
   - `start_learning_counter` - Rate of learning clicks
   - `explore_courses_counter` - Rate of exploration clicks
   - Combined event rates and trends

## 🐳 Docker Deployment (Optional)

Create a `docker-compose.yml` for easy deployment:

```yaml
version: "3.8"
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092

  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

## 🔍 Troubleshooting

### Common Issues

1. **Kafka Connection Issues**

   - Verify Kafka is running: `netstat -tlnp | grep 9092`
   - Check firewall settings
   - Ensure correct IP address in configuration

2. **Prometheus Metrics Not Appearing**

   - Verify ConsumerService is running on port 8081
   - Check Prometheus configuration targets
   - Ensure metrics endpoint is accessible: `curl http://localhost:8082/metrics`

3. **Frontend CORS Issues**
   - CORS is configured in [`ProducerService/app/src/main/java/org/example/config/CorsConfig.java`](ProducerService/app/src/main/java/org/example/config/CorsConfig.java)
   - Update allowed origins if running on different domains

### Logs and Debugging

```bash
# Check Kafka topics
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Monitor Kafka messages
bin/kafka-console-consumer.sh --topic tracking-events --from-beginning --bootstrap-server localhost:9092

# Check service logs
./gradlew bootRun --info  # For detailed Spring Boot logs
```

## 🚀 Production Deployment

### AWS EC2 Deployment

1. Launch EC2 instance (t3.medium or larger)
2. Configure security groups:
   - Port 8080 (ProducerService)
   - Port 8081 (ConsumerService)
   - Port 9092 (Kafka)
   - Port 9090 (Prometheus)
   - Port 3000 (Grafana)
3. Follow Ubuntu VM setup instructions
4. Use Elastic Load Balancer for high availability

### Scaling Considerations

- Use Kafka clusters for high throughput
- Deploy multiple ConsumerService instances
- Use managed services (AWS MSK, CloudWatch, etc.)
- Implement proper logging and monitoring

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📝 License

This project is open-source and available under the MIT License.

## 📧 Support

For issues and questions:

- Create an issue in the repository
- Check the troubleshooting section
- Review logs for detailed error messages
