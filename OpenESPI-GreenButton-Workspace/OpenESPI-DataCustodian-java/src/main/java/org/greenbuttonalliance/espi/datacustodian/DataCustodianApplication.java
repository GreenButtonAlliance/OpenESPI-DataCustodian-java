/*
 *
 *    Copyright (c) 2018-2025 Green Button Alliance, Inc.
 *
 *    Portions (c) 2013-2018 EnergyOS.org
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package org.greenbuttonalliance.espi.datacustodian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot application class for the OpenESPI Data Custodian Resource Server.
 * 
 * This application provides a NAESB ESPI 1.0 compliant Data Custodian (Utility) implementation
 * that serves as an OAuth2 Resource Server, consuming authorization tokens from the separate
 * OpenESPI Authorization Server.
 * 
 * Key Features:
 * - OAuth2 Resource Server for ESPI-compliant API access
 * - RESTful endpoints for Green Button data exchange
 * - Multi-database support (MySQL, PostgreSQL, H2)
 * - Spring Boot 3.5 with Jakarta EE 10 compliance
 * - Integration with OpenESPI-Common domain models and services
 */
@SpringBootApplication(scanBasePackages = {
    "org.greenbuttonalliance.espi.datacustodian",
    "org.greenbuttonalliance.espi.common"
})
@EntityScan(basePackages = {
    "org.greenbuttonalliance.espi.common.domain.usage",
    "org.greenbuttonalliance.espi.common.domain.customer"
})
@EnableJpaRepositories(basePackages = {
    "org.greenbuttonalliance.espi.common.repositories"
})
@EnableTransactionManagement
public class DataCustodianApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataCustodianApplication.class, args);
    }
}