[![Build Status](https://travis-ci.org/energyos/OpenESPI-DataCustodian-java.png)](https://travis-ci.org/energyos/OpenESPI-DataCustodian-java)

OpenESPI-DataCustodian
======================

The Open Energy Services Provider Interface (ESPI) Data Custodian Repository Providing implementations of the interface used to provide energy usage information to retail customers and third parties.

## Setup

First clone the project from github:

```bash
git clone https://github.com/energyos/OpenESPI-DataCustodian.git
cd OpenESPI-DataCustodian/
```

Start tomcat7 using maven:

```bash
mvn tomcat7:run
```

Now the application should be available at [http://localhost:8080/retailcustomers](http://localhost:8080/retailcustomers).

### Eclipse Setup

Open Eclipse and import a Maven project (File > Import... > Maven > Existing Maven Projects).

### Spring Tool Suite Setup

Open Spring Tool Suite and import a Maven project (File > Import... > Maven > Existing Maven Projects).

### IntelliJ Setup

Open IntelliJ and open the project (File > Open...).
