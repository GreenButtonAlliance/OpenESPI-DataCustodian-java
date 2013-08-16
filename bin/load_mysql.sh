#!/bin/sh

echo "\. src/main/resources/db/mysql/initDB.sql" | mysql -u root -p datacustodian
