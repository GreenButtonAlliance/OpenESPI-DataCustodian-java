#!/usr/bin/env sh
git clone git://github.com/energyos/OpenESPI-Common-java.git common
pushd common
mvn clean install
popd
