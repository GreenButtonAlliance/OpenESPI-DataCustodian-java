#!/bin/sh

psql datacustodian -f "src/main/resources/db/postgres/initDB.sql"
