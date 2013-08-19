#!/bin/sh

createdb datacustodian
wait $!
psql datacustodian -f "src/main/resources/db/postgres/initDB.sql"
