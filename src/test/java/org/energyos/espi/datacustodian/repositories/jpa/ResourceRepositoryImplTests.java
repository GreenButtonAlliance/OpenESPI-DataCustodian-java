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


import org.energyos.espi.common.domain.*;
import org.energyos.espi.common.models.atom.LinkType;
import org.energyos.espi.common.repositories.ResourceRepository;
import org.energyos.espi.common.test.EspiPersistenceFactory;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ResourceRepositoryImplTests {
    @Autowired
    public EspiPersistenceFactory factory;

    @Autowired
    public ResourceRepository repository;

    @Test
    public void persist() {
        UsagePoint usagePoint = EspiFactory.newSimpleUsagePoint();

        repository.persist(usagePoint);

        assertThat(usagePoint.getId(), is(notNullValue()));
    }

    @Test
    public void findByRelatedHref() {
        UsagePoint usagePoint = EspiFactory.newSimpleUsagePoint();
        usagePoint.getRelatedLinks().add(new LinkType(LinkType.RELATED, LinkType.HREF));

        repository.persist(usagePoint);

        assertThat(repository.findAllParentsByRelatedHref(LinkType.HREF, usagePoint).get(0).getId(), equalTo(usagePoint.getId()));
    }

    @Test
    public void findAllRelated() throws Exception {
        MeterReading meterReading = EspiFactory.newMeterReading();
        meterReading.setUpLink(new LinkType(LinkType.UP, LinkType.HREF));
        repository.persist(meterReading);

        UsagePoint usagePoint = EspiFactory.newSimpleUsagePoint();
        usagePoint.getRelatedLinks().add(new LinkType(LinkType.RELATED, LinkType.HREF));
        repository.persist(usagePoint);

        assertThat(repository.findAllRelated(usagePoint), hasItem(meterReading));
    }

    @Test
    public void findByUUID_returnsUsagePoint() throws Exception {
        UsagePoint usagePoint = factory.createUsagePoint();

        assertThat(usagePoint, equalTo(repository.findByUUID(usagePoint.getUUID(), UsagePoint.class)));
    }

    @Test
    public void findByUUID_returnsMeterReading() throws Exception {
        MeterReading meterReading = factory.createMeterReading();

        assertThat(meterReading, equalTo(repository.findByUUID(meterReading.getUUID(), MeterReading.class)));
    }
    
    

    @Test
    public void findById_returnsUsagePoint() throws Exception {
        UsagePoint usagePoint = factory.createUsagePoint();

        assertThat(repository.findById(usagePoint.getId(), UsagePoint.class), equalTo(usagePoint));
    }

    @Test
    public void findById_returnsMeterReading() throws Exception {
        MeterReading meterReading = factory.createMeterReading();

        assertThat(repository.findById(meterReading.getId(), MeterReading.class), equalTo(meterReading));
    }

    @Test
    public void findById_returnsElectricPowerUsageSummary() throws Exception {
        ElectricPowerUsageSummary electricPowerUsageSummary = factory.createUsagePoint().getElectricPowerUsageSummaries().get(0);

        assertThat(repository.findById(electricPowerUsageSummary.getId(), ElectricPowerUsageSummary.class), equalTo(electricPowerUsageSummary));
    }

    @Test
    public void findById_returnsElectricPowerQualitySummary() throws Exception {
        ElectricPowerQualitySummary electricPowerQualitySummary = factory.createUsagePoint().getElectricPowerQualitySummaries().get(0);

        assertThat(repository.findById(electricPowerQualitySummary.getId(), ElectricPowerQualitySummary.class), equalTo(electricPowerQualitySummary));
    }

    @Test
    public void findById_returnsReadingType() throws Exception {
        ReadingType readingType = factory.createMeterReading().getReadingType();

        assertThat(repository.findById(readingType.getId(), ReadingType.class), equalTo(readingType));
    }

    @Test
    public void findById_returnsIntervalBlock() throws Exception {
        IntervalBlock intervalBlock = factory.createMeterReading().getIntervalBlocks().get(0);

        assertThat(repository.findById(intervalBlock.getId(), IntervalBlock.class), equalTo(intervalBlock));
    }

    @Test
    public void findById_returnsTimeConfiguration() throws Exception {
        TimeConfiguration localTimeParameters = factory.createUsagePoint().getLocalTimeParameters();

        assertThat(repository.findById(localTimeParameters.getId(), TimeConfiguration.class), equalTo(localTimeParameters));
    }

}
