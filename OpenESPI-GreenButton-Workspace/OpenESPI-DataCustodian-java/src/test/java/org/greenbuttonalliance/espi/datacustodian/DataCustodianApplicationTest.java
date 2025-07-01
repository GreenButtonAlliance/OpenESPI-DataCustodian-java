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

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for the OpenESPI Data Custodian Spring Boot application.
 * 
 * This test verifies that the application context loads successfully with
 * all Spring Boot 3.5 configurations and dependencies.
 */
@SpringBootTest
@ActiveProfiles("test")
class DataCustodianApplicationTest {

    /**
     * Test that the Spring Boot application context loads successfully.
     * This verifies that all configuration classes, beans, and dependencies
     * are properly configured and can be instantiated.
     */
    @Test
    void contextLoads() {
        // This test will pass if the application context loads without errors
        // It validates the entire Spring Boot configuration including:
        // - Security configuration
        // - JPA configuration  
        // - Web configuration
        // - Service layer beans
        // - Repository layer beans
    }
}