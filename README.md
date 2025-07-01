[![CircleCI](https://circleci.com/gh/GreenButtonAlliance/OpenESPI-DataCustodian-java/tree/master.svg?style=svg)](https://circleci.com/gh/GreenButtonAlliance/OpenESPI-DataCustodian-java/tree/master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=GreenButtonAlliance_OpenESPI-DataCustodian-java&metric=alert_status)](https://sonarcloud.io/dashboard?id=GreenButtonAlliance_OpenESPI-DataCustodian-java)


# OpenESPI-DataCustodian -- Archived July 1, 2025

NOTE: This repository is no longer maintained. The original OpenESPI project has been archived and is no longer being
maintained.  The original code is being migrated to Spring Boot 3.5 with a modernized architecture.  The new code is
being developed in the [OpenESPI-GreenButton-Java](https://Github.com/GreenButtonAlliance/OpenESPI-GreenButton-Java),
as a monorepo that contains the Data Custodian, Third Party, Common, and OAuth2 Authorization Server modules.
The Open Energy Services Provider Interface (ESPI) Data Custodian Repository Providing implementations of the interface used to provide energy usage information to retail customers and third parties.

An operational sandbox with these services operating may be found at:
<a href="https://sandbox.greenbuttonalliance.org:8443/DataCustodian">sandbox.greenbuttonalliance.org:8443/DataCustodian</a>

## Setup

Note: You need to download and install [OpenESPI-Common-java](https://github.com/GreenButtonAlliance/OpenESPI-Common-java) into your local Maven repository to build this project.

First clone the project from github:

```bash
git clone https://github.com/GreenButtonAlliance/OpenESPI-DataCustodian-java.git
cd OpenESPI-DataCustodian/
```


Build and start tomcat7 using maven (note: you must have first built the OpenESPI-Common-java jar):

```bash
mvn tomcat7:run
```

Now the application should be available at [http://localhost:8080/retailcustomers](http://localhost:8080/DataCustodian).

## Building
```bash
# for the default test profile
mvn clean install

# or for a specific profile
mvn -P <profile name> -Dmaven.test.skip=true clean install
```

## IDE Setup

### Eclipse Setup

Open Eclipse and import a Maven project (File > Import... > Maven > Existing Maven Projects).

### Spring Tool Suite Setup

Open Spring Tool Suite and import a Maven project (File > Import... > Maven > Existing Maven Projects).
To Run from within STS:

right click on project and select RunOnServer


To run the DC and/or the TP:

do a maven build and install accordingly. Then the WAR files will be in the right position.

To Start server:

```bash
sudo /home/bitnami/springsource/vfabric-tc-server-developer-2.9.3.RELEASE/base-instance/bin/tcruntime-ctl.sh start
```
To Stop server:

```bash
sudo /home/bitnami/springsource/vfabric-tc-server-developer-2.9.3.RELEASE/base-instance/bin/tcruntime-ctl.sh stop
```

### IntelliJ Setup

Open IntelliJ and open the project (File > Open...).

## Testing

### Unit Tests

To run all Unit tests:

```bash
mvn test
```

Run a single test class:

```bash
mvn -Dtest=<TestClassName> test
mvn -Dtest=HomeControllerTests test
```

Run a single test in a single class:

```bash
mvn -Dtest=<TestClassName>#<testMethodName> testMethodName
mvn -Dtest=HomeControllerTests#index_shouldDisplayHomePage test
```

### Cucumber Features

To run all Cucumber features:

```bash
mvn verify
```
