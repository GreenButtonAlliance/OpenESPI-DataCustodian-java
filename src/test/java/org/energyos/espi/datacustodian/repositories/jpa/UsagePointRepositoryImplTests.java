/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.energyos.espi.datacustodian.repositories.jpa;


import org.energyos.espi.datacustodian.domain.*;
import org.energyos.espi.datacustodian.repositories.RetailCustomerRepository;
import org.energyos.espi.datacustodian.repositories.UsagePointRepository;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class UsagePointRepositoryImplTests {

    @Autowired
    UsagePointRepository repository;
    @Autowired
    RetailCustomerRepository retailCustomerRepository;
    private RetailCustomer customer;
    private UUID uuid;

    @Before
    public void setup() {
        customer = new RetailCustomer();
        customer.setId(1L);
        uuid = UUID.randomUUID();
    }

    @Test
    public void findByRetailCustomer_returnsUsagePointsByCustomer() {
        RetailCustomer customer1 = EspiFactory.newRetailCustomer();
        retailCustomerRepository.persist(customer1);

        UsagePoint usagePoint = EspiFactory.newUsagePoint(customer1);
        repository.persist(usagePoint);

        assertEquals(1, repository.findAllByRetailCustomerId(customer1.getId()).size());
    }

    @Test
    public void findById_returnsUsagePoint() {
        UsagePoint usagePoint = repository.findById(1L);
        assertNotNull("The usage point was null.", usagePoint);
    }

    @Test
    public void persist_withNewUsagePoint_persistsUsagePoint() throws Exception {
        UsagePoint usagePoint = getUsagePoint();

        repository.persist(usagePoint);

        assertNotNull(usagePoint.getId());
        assertNotNull(usagePoint.getRetailCustomer());
    }

    private UsagePoint getUsagePoint() {
        UsagePoint usagePoint = EspiFactory.newUsagePoint();
        usagePoint.setMRID("urn:uuid:E8E75691-7F9D-49F3-8BE2-3A74EBF6BFC0");
        return usagePoint;
    }

    @Test
    public void persist_savesMeterReading() {
        UsagePoint usagePoint = getUsagePoint();
        MeterReading meterReading = new MeterReading();

        usagePoint.addMeterReading(meterReading);

        repository.persist(usagePoint);

        assertNotNull("MeterReading id was null", usagePoint.getMeterReadings().get(0).getId());
        assertNotNull("MeterReading usagePoint is null", usagePoint.getMeterReadings().get(0).getUsagePoint());
    }

    @Test
    public void persist_savesIntervalBlocks() {
        UsagePoint usagePoint = getUsagePoint();
        MeterReading meterReading = new MeterReading();
        IntervalBlock intervalBlock = new IntervalBlock();

        meterReading.getIntervalBlocks().add(intervalBlock);
        usagePoint.getMeterReadings().add(meterReading);

        repository.persist(usagePoint);

        assertNotNull(usagePoint.getMeterReadings().get(0).getIntervalBlocks().get(0).getId());
    }

    @Test
    public void createOrReplaceByUUID_givenNewUsagePoint_savesUsagePoint() {
        UsagePoint usagePoint = EspiFactory.newUsagePointOnly(UUID.randomUUID());
        repository.createOrReplaceByUUID(usagePoint);

        assertNotNull(usagePoint.getId());
    }

    @Test
    public void createOrReplaceByUUID_givenExistingUsagePoint_updatesUsagePoint() {
        UsagePoint usagePoint = EspiFactory.newUsagePointOnly(uuid);
        repository.persist(usagePoint);

        UsagePoint updatedUsagePoint = EspiFactory.newUsagePointOnly(uuid);
        repository.createOrReplaceByUUID(updatedUsagePoint);

        assertEquals(usagePoint.getId(), updatedUsagePoint.getId());
    }

    @Test
    public void createOrReplaceByUUID_givenExistingUsagePoint_updatesServiceCategory() {
        UsagePoint usagePoint = EspiFactory.newUsagePointOnly(uuid);
        repository.persist(usagePoint);

        UsagePoint updatedUsagePoint = EspiFactory.newUsagePointOnly(uuid);
        updatedUsagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.GAS_SERVICE));
        repository.createOrReplaceByUUID(updatedUsagePoint);

        updatedUsagePoint = repository.findById(updatedUsagePoint.getId());

        assertEquals(ServiceCategory.GAS_SERVICE, updatedUsagePoint.getServiceCategory().getKind());
    }

    @Test
    public void createOrReplaceByUUID_givenUsagePointWithNoCustomer_doesNotUpdateRetailCustomer() {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerRepository.persist(retailCustomer);
        UsagePoint usagePoint = EspiFactory.newUsagePoint(retailCustomer);
        repository.persist(usagePoint);

        UsagePoint updatedUsagePoint = new UsagePoint();
        updatedUsagePoint.setUUID(usagePoint.getUUID());
        repository.createOrReplaceByUUID(updatedUsagePoint);

        updatedUsagePoint = repository.findById(updatedUsagePoint.getId());

        assertEquals(retailCustomer.getId(), updatedUsagePoint.getRetailCustomer().getId());
    }

    @Test
    public void createOrReplaceByUUID_givenUsagePointWithCustomer_updatesRetailCustomer() {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerRepository.persist(retailCustomer);
        UsagePoint usagePoint = EspiFactory.newUsagePoint(retailCustomer);
        repository.createOrReplaceByUUID(usagePoint);

        RetailCustomer newRetailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerRepository.persist(newRetailCustomer);
        UsagePoint updatedUsagePoint = EspiFactory.newUsagePoint(newRetailCustomer);
        updatedUsagePoint.setUUID(usagePoint.getUUID());

        repository.createOrReplaceByUUID(updatedUsagePoint);

        updatedUsagePoint = repository.findById(updatedUsagePoint.getId());

        assertEquals(newRetailCustomer.getId(), updatedUsagePoint.getRetailCustomer().getId());
    }

    @Test
    public void createOrReplaceByUUID_replacesDescription() {
        UsagePoint usagePoint = EspiFactory.newUsagePointOnly(uuid);
        repository.persist(usagePoint);

        UsagePoint updatedUsagePoint = EspiFactory.newUsagePointOnly(uuid);
        updatedUsagePoint.setDescription("New description");

        repository.createOrReplaceByUUID(updatedUsagePoint);

        usagePoint = repository.findByUUID(uuid);

        assertEquals("New description", usagePoint.getDescription());
    }

    @Test
    public void createOrReplaceByUUID_replacesServiceCategory() {
        UsagePoint usagePoint = EspiFactory.newUsagePointOnly(uuid);

        repository.persist(usagePoint);

        UsagePoint updatedUsagePoint = EspiFactory.newUsagePointOnly(uuid);
        updatedUsagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.GAS_SERVICE));

        repository.createOrReplaceByUUID(updatedUsagePoint);

        usagePoint = repository.findByUUID(uuid);

        assertEquals(ServiceCategory.GAS_SERVICE, usagePoint.getServiceCategory().getKind());
    }

    @Test
    public void createOrReplaceByUUID_replacesMeterReadings() {
        UsagePoint usagePoint = EspiFactory.newUsagePointOnly(uuid);
        usagePoint.addMeterReading(new MeterReading());

        assertTrue(usagePoint.getMeterReadings().size() > 0);

        repository.persist(usagePoint);

        UsagePoint updatedUsagePoint = EspiFactory.newUsagePointOnly(uuid);
        updatedUsagePoint.addMeterReading(new MeterReading());
        updatedUsagePoint.addMeterReading(new MeterReading());

        repository.createOrReplaceByUUID(updatedUsagePoint);

        usagePoint = repository.findByUUID(uuid);

        assertTrue(usagePoint.getMeterReadings().size() == 2);
    }

    @Test
    public void createOrReplaceByUUID_replacesElectricPowerUsageSummaries() {
        UsagePoint usagePoint = EspiFactory.newUsagePointOnly(uuid);

        repository.persist(usagePoint);

        UsagePoint updatedUsagePoint = EspiFactory.newUsagePointOnly(uuid);
        updatedUsagePoint.addElectricPowerUsageSummary(new ElectricPowerUsageSummary());

        repository.createOrReplaceByUUID(updatedUsagePoint);

        usagePoint = repository.findByUUID(uuid);

        assertTrue(usagePoint.getElectricPowerUsageSummaries().size() == 1);
    }

    @Test
    public void findByUUID() {
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setMRID("urn:uuid:E8E75691-7F9D-49F3-8BE2-3A74EBF6BFC0");
        usagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.ELECTRICITY_SERVICE));
        repository.persist(usagePoint);

        assertEquals(usagePoint, repository.findByUUID(usagePoint.getUUID()));
    }

    @Test
    public void associateByUUID_setsRetailCustomer() {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerRepository.persist(retailCustomer);

        UsagePoint usagePoint = EspiFactory.newUsagePointOnly(uuid);
        repository.persist(usagePoint);

        repository.associateByUUID(retailCustomer, uuid);

        assertEquals(retailCustomer.getId(), usagePoint.getRetailCustomer().getId());
    }

    @Test
    public void associateByUUID_retainsDescription() {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerRepository.persist(retailCustomer);

        UsagePoint usagePoint = EspiFactory.newUsagePointOnly(uuid);
        String description = usagePoint.getDescription();
        repository.persist(usagePoint);

        repository.associateByUUID(retailCustomer, uuid);

        assertEquals(description, usagePoint.getDescription());
    }

    @Test
    public void associateByUUID_retainsMeterReadings() {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerRepository.persist(retailCustomer);

        UsagePoint usagePoint = EspiFactory.newUsagePointOnly(uuid);
        usagePoint.addMeterReading(new MeterReading());
        repository.persist(usagePoint);

        repository.associateByUUID(retailCustomer, uuid);
        assertTrue(usagePoint.getMeterReadings().size() > 0);
    }

    public void persist_savesReadingTypes() throws Exception {
        UsagePoint usagePoint = EspiFactory.newUsagePoint();
        MeterReading meterReading = new MeterReading();
        ReadingType readingType = EspiFactory.newReadingType();

        usagePoint.addMeterReading(meterReading);
        meterReading.setReadingType(readingType);

        repository.persist(usagePoint);

        assertNotNull("ReadingType id was null", readingType.getId());
    }
}
