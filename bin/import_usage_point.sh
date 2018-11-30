#!/bin/sh
mvn -q exec:java -Dexec.mainClass=ImportUsagePoint -Dexec.args="$1 $2"
