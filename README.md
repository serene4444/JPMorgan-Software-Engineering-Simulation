# JPMorgan Software Engineering Simulation - Midas Core

## Overview
This project implements a transaction processing system called "Midas Core" that integrates with Kafka for message processing and H2 database for data persistence. The system processes financial transactions with validation, balance updates, and incentive calculations.

## Architecture

### Core Components
- **MidasCoreApplication**: Main Spring Boot application
- **TransactionListener**: Kafka consumer that processes incoming transactions
- **TransactionService**: Business logic for transaction validation and processing
- **IncentiveService**: Integration with external incentive API
- **UserRecord**: JPA entity representing users with balances
- **TransactionRecord**: JPA entity storing transaction history with incentives

### Key Features
- ✅ **Transaction Validation**: Validates sender/recipient existence and sufficient balance
- ✅ **Database Integration**: H2 in-memory database with JPA/Hibernate
- ✅ **Kafka Integration**: Real-time transaction processing via Kafka
- ✅ **Incentive API Integration**: External API calls for transaction incentives
- ✅ **Balance Management**: Automatic balance updates with incentive calculations

## Project Structure

```
src/
├── main/java/com/jpmc/midascore/
│   ├── MidasCoreApplication.java          # Main application class
│   ├── TransactionListener.java           # Kafka message listener
│   ├── entity/
│   │   ├── UserRecord.java               # User entity with balance
│   │   └── TransactionRecord.java        # Transaction entity with incentives
│   ├── foundation/
│   │   ├── Transaction.java              # Transaction DTO
│   │   └── Incentive.java                # Incentive DTO
│   ├── repository/
│   │   ├── UserRepository.java           # User data access
│   │   └── TransactionRecordRepository.java # Transaction data access
│   └── service/
│       ├── TransactionService.java       # Transaction processing logic
│       └── IncentiveService.java         # Incentive API integration
└── test/java/com/jpmc/midascore/
    ├── TaskOneTests.java                 # Basic functionality tests
    ├── TaskTwoTests.java                 # Advanced feature tests
    ├── TaskThreeTests.java               # Database integration tests
    ├── TaskFourTests.java                # Incentive API integration tests
    └── TaskFiveTests.java                # Final comprehensive tests
```

## Prerequisites

- **Java 17** or higher
- **Maven** (or use included Maven wrapper)
- **Kafka** (embedded in tests)

## Setup and Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd JPMorgan-Software-Engineering-Simulation
```

### 2. Set Java Environment
```bash
# Set JAVA_HOME (Windows)
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.16.8-hotspot"
$env:PATH += ";C:\Program Files\Microsoft\jdk-17.0.16.8-hotspot\bin"

# Verify Java installation
java -version
```

### 3. Build the Project
```bash
# Using Maven wrapper
.\mvnw.cmd clean compile

# Or using Maven directly
mvn clean compile
```

## Running the Application

### 1. Start the Incentive API (Optional)
```bash
# In a separate terminal
java -jar services/transaction-incentive-api.jar
```
The incentive API runs on `http://localhost:8080/incentive`

### 2. Run Tests
```bash
# Run specific test
.\mvnw.cmd test -Dtest=TaskThreeTests
.\mvnw.cmd test -Dtest=TaskFourTests

# Run all tests
.\mvnw.cmd test
```

## Configuration

### Application Properties (`application.yml`)
```yaml
general:
  kafka-topic: transactions

spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: midas-core-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.jpmc.midascore.foundation"
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
  h2:
    console:
      enabled: true
```

## API Endpoints

### Transaction Processing
- **Kafka Topic**: `transactions`
- **Message Format**: JSON serialized `Transaction` object
- **Processing**: Automatic validation, balance updates, and incentive calculation

### Incentive API Integration
- **Endpoint**: `http://localhost:8080/incentive`
- **Method**: POST
- **Request**: JSON `Transaction` object
- **Response**: JSON `Incentive` object with `amount` field

## Transaction Processing Flow

1. **Receive Transaction**: Kafka listener receives transaction message
2. **Validate Transaction**:
   - Check sender exists
   - Check recipient exists
   - Verify sender has sufficient balance
3. **Get Incentive**: Call external incentive API
4. **Update Balances**:
   - Sender: `balance - transaction_amount`
   - Recipient: `balance + transaction_amount + incentive_amount`
5. **Save Transaction**: Store transaction record with incentive in database

## Database Schema

### UserRecord Table
```sql
CREATE TABLE user_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    balance FLOAT NOT NULL
);
```

### TransactionRecord Table
```sql
CREATE TABLE transaction_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    amount FLOAT NOT NULL,
    incentive FLOAT NOT NULL,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES user_record(id),
    FOREIGN KEY (recipient_id) REFERENCES user_record(id)
);
```

## Test Data

### User Data (`/test_data/lkjhgfdsa.hjkl`)
```
bernie, 1200.23
grommit, 2215.37
maria, 2774.14
mario, 12.34
waldorf, 444.55
whosit, 888.90
whatsit, 777.60
howsit, 68.70
wilbur, 3476.21
antonio, 2121.54
calypso, 779421.33
```

### Transaction Data
- **Task Three**: `/test_data/mnbvcxz.vbnm` (22 transactions)
- **Task Four**: `/test_data/alskdjfh.fhdjsk` (22 transactions)

## Key Implementation Details

### Transaction Validation
```java
// Validate sender exists
UserRecord sender = userRepository.findById(transaction.getSenderId());
if (sender == null) {
    logger.warn("Transaction rejected: sender with ID {} does not exist", transaction.getSenderId());
    return false;
}

// Validate recipient exists
UserRecord recipient = userRepository.findById(transaction.getRecipientId());
if (recipient == null) {
    logger.warn("Transaction rejected: recipient with ID {} does not exist", transaction.getRecipientId());
    return false;
}

// Validate sufficient balance
if (sender.getBalance() < transaction.getAmount()) {
    logger.warn("Transaction rejected: insufficient balance");
    return false;
}
```

### Balance Updates with Incentives
```java
// Get incentive from API
Incentive incentive = incentiveService.getIncentive(transaction);

// Update balances
sender.setBalance(sender.getBalance() - transaction.getAmount());
recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentive.getAmount());
```

### Error Handling
- **Incentive API Failures**: Returns 0.0 incentive and continues processing
- **Database Errors**: Transaction rollback with proper error logging
- **Invalid Transactions**: Rejected with detailed logging

## Troubleshooting

### Common Issues

1. **Java Not Found**
   ```bash
   # Install Java 17
   winget install Microsoft.OpenJDK.17
   
   # Set environment variables
   $env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.16.8-hotspot"
   $env:PATH += ";C:\Program Files\Microsoft\jdk-17.0.16.8-hotspot\bin"
   ```

2. **Incentive API Connection Refused**
   - Start the incentive API: `java -jar services/transaction-incentive-api.jar`
   - Check if port 8080 is available
   - API will return 0.0 incentives if not running (graceful degradation)

3. **Kafka Connection Issues**
   - Tests use embedded Kafka, no external setup required
   - Check if port 9092 is available

4. **Database Issues**
   - H2 database is in-memory and auto-created
   - Check H2 console at `http://localhost:8080/h2-console` (if enabled)

## Test Results

### Task Three (Database Integration)
- **Target**: Calculate waldorf's balance after all transactions
- **Result**: 627 (rounded down from 627.86)
- **Key**: Transaction validation and balance updates working correctly

### Task Four (Incentive Integration)
- **Target**: Calculate wilbur's balance after all transactions with incentives
- **Result**: 3089 (rounded down from 3089.42)
- **Key**: Incentive API integration and enhanced balance calculations

## Dependencies

### Core Dependencies
- **Spring Boot 3.2.5**: Application framework
- **Spring Data JPA**: Database access
- **Spring Kafka**: Message processing
- **H2 Database**: In-memory database
- **Jackson**: JSON serialization

### Test Dependencies
- **Spring Boot Test**: Testing framework
- **Spring Kafka Test**: Kafka testing utilities
- **Testcontainers**: Container-based testing

## Development Notes

- **Transaction Isolation**: All transaction processing is wrapped in `@Transactional`
- **Error Resilience**: Graceful handling of API failures
- **Logging**: Comprehensive logging for debugging and monitoring
- **Testing**: Embedded Kafka and H2 for isolated testing

## Future Enhancements

- [ ] Add REST API endpoints for transaction queries
- [ ] Implement transaction history pagination
- [ ] Add user management endpoints
- [ ] Implement transaction analytics
- [ ] Add monitoring and metrics
- [ ] Implement transaction retry mechanisms

## License

This project is part of the JPMorgan Software Engineering Simulation program.

---

**Note**: This is a simulation project for educational purposes. The implementation demonstrates real-world software engineering practices including microservices integration, event-driven architecture, and database management.
