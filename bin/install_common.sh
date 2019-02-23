#!/usr/bin/env sh
git clone https://github.com/GreenButtonAlliance/OpenESPI-Common-java.git common
cd common
mvn clean -DskipTests=true package 
cd ..
