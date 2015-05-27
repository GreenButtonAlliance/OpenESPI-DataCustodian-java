#!/usr/bin/env sh
git clone https://github.com/energyos/OpenESPI-Common-java.git common
cd common
mvn clean -DskipTests=true package 
cd ..
