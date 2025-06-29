[![Build Status](https://img.shields.io/badge/Build-Passing-success?style=flat&logo=github-actions)](https://github.com/GreenButtonAlliance/OpenESPI-DataCustodian-java/actions)
[![CI/CD Pipeline](https://github.com/GreenButtonAlliance/OpenESPI-DataCustodian-java/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/GreenButtonAlliance/OpenESPI-DataCustodian-java/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=GreenButtonAlliance_OpenESPI-DataCustodian-java&metric=alert_status)](https://sonarcloud.io/dashboard?id=GreenButtonAlliance_OpenESPI-DataCustodian-java)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen?style=flat&logo=spring)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?style=flat&logo=openjdk)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue?style=flat&logo=apache-maven)](https://maven.apache.org/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-9+-purple?style=flat&logo=eclipse)](https://jakarta.ee/)
[![Hibernate](https://img.shields.io/badge/Hibernate-6.x-yellow?style=flat&logo=hibernate)](https://hibernate.org/)
[![MapStruct](https://img.shields.io/badge/MapStruct-1.6.0-orange?style=flat)](https://mapstruct.org/)
[![Lombok](https://img.shields.io/badge/Lombok-1.18.34-red?style=flat)](https://projectlombok.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey?style=flat&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![NAESB](https://img.shields.io/badge/NAESB-ESPI%20ver.%204.0-blue?style=flat)](https://www.naesb.org/)

# OpenESPI-DataCustodian

A modern Spring Boot 3.5 implementation of the Open Energy Services Provider Interface (ESPI) Data Custodian, providing OAuth2 Resource Server capabilities for secure energy usage data access according to the North American Energy Standards Board (NAESB) REQ.21 ESPI specification.

## Overview

The OpenESPI-DataCustodian serves as a secure OAuth2 Resource Server that:

- **Protects energy usage data** with scope-based authorization (FB=1_3_4_5_13_14_15_39, FB=4_5_15)
- **Provides RESTful APIs** for retail customers and third-party applications
- **Implements ESPI 4.0 specification** with ATOM XML data formats
- **Supports multiple databases** (MySQL, PostgreSQL, H2) with Flyway migrations
- **Offers modern web interface** with Thymeleaf templates and Spring Security 6

## Architecture

Built on **Spring Boot 3.5** with modern architectural patterns:

- **OAuth2 Resource Server** with opaque token introspection
- **Entity-based data model** with UUID primary keys (48-bit+ ESPI compliance)  
- **Service layer architecture** with DTO mapping via MapStruct
- **Multi-profile configuration** for development, testing, and production
- **Comprehensive testing** with TestContainers and Cucumber BDD

## Quick Start

### Prerequisites

- **Java 21+** (OpenJDK recommended)
- **Maven 3.9+** 
- **MySQL 8.0+** or **PostgreSQL 15+** (for production)
- **OpenESPI-Common-java** dependency (built automatically)

### Clone and Build

```bash
git clone https://github.com/GreenButtonAlliance/OpenESPI-DataCustodian-java.git
cd OpenESPI-DataCustodian-java

# Build with default MySQL profile
mvn clean install

# Or skip tests for faster build
mvn clean install -DskipTests
```

### Run Application

```bash
# Start with MySQL (default)
mvn spring-boot:run

# Start with PostgreSQL
mvn spring-boot:run -Pdev-postgresql

# Start with H2 (local development)
mvn spring-boot:run -Plocal
```

The application will be available at: **http://localhost:8080**

## Configuration Profiles

| Profile | Database | Use Case |
|---------|----------|----------|
| `dev-mysql` | MySQL | Default development (active by default) |
| `dev-postgresql` | PostgreSQL | PostgreSQL development |
| `local` | H2 | Local development/testing |
| `prod` | MySQL | Production deployment |
| `docker` | MySQL | Container deployment |
| `aws-sandbox` | MySQL | AWS GBA Sandbox |

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests with TestContainers
```bash
# MySQL integration tests
mvn verify -Ptestcontainers-mysql

# PostgreSQL integration tests  
mvn verify -Ptestcontainers-postgresql
```

### BDD Tests with Cucumber
```bash
mvn verify
```

### Code Coverage
```bash
mvn verify jacoco:report
# Reports available in target/site/jacoco/
```

## API Documentation

Interactive API documentation is available via **Swagger UI**:
- **Development**: http://localhost:8080/swagger-ui.html
- **Production**: Configure accordingly per environment

## Security

This implementation follows **OAuth2 Resource Server** patterns:

- **Opaque access tokens** (no JWT) per ESPI specification
- **Scope-based authorization** with fine-grained permissions
- **Spring Security 6** with modern security configurations
- **OWASP dependency scanning** integrated in CI/CD

## Development

### IDE Setup

**IntelliJ IDEA** (Recommended):
```bash
# Open project
File → Open → select pom.xml

# Enable annotation processing for Lombok/MapStruct
Settings → Build → Compiler → Annotation Processors → Enable
```

**Eclipse/Spring Tool Suite**:
```bash
File → Import → Maven → Existing Maven Projects
```

### Code Quality

```bash
# Run OWASP security scan
mvn org.owasp:dependency-check-maven:check

# Static analysis with SpotBugs
mvn compile spotbugs:check
```

## Deployment

### Docker
```bash
# Build application JAR
mvn clean package -DskipTests

# Run with Docker profile
java -jar target/OpenESPI-DataCustodian.jar --spring.profiles.active=docker
```

### Production
Configure production database and OAuth2 authorization server endpoints in:
- `src/main/resources/application-prod.yml`
- Environment variables or external configuration

## Contributing

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Follow** code style and add tests
4. **Commit** changes (`git commit -m 'Add amazing feature'`)
5. **Push** to branch (`git push origin feature/amazing-feature`)
6. **Open** a Pull Request

## License

Licensed under the **Apache License 2.0**. See [LICENSE](LICENSE) for details.

## Support

- **Issues**: [GitHub Issues](https://github.com/GreenButtonAlliance/OpenESPI-DataCustodian-java/issues)
- **Documentation**: [Green Button Alliance](https://www.greenbuttonalliance.org)
- **Sandbox**: [https://sandbox.greenbuttonalliance.org:8443/DataCustodian](https://sandbox.greenbuttonalliance.org:8443/DataCustodian)
