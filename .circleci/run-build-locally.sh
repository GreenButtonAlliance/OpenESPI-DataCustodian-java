#!/usr/bin/env bash
curl --user ${CIRCLE_TOKEN}: \
     --request POST \
     --form revision=${CIRCLE_SHA1} \
     --form config=@config.yml \
     --form notify=false \
         https://circleci.com/api/v1.1/project/github.com/GreenButtonAlliance/OpenESPI-DataCustodian-java/tree/develop