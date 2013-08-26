#!/bin/sh
mvn -q exec:java -Dexec.mainClass=org.energyos.espi.datacustodian.console.ImportUsagePoint -Dexec.args="$1 $2"
