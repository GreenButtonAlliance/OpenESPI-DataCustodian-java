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

package org.energyos.espi.common.service.impl;


import org.energyos.espi.common.domain.MeterReading;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.repositories.ResourceRepository;
import org.energyos.espi.datacustodian.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.energyos.espi.common.test.EspiFactory.newMeterReading;
import static org.energyos.espi.common.test.EspiFactory.newUsagePoint;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.verify;

public class ResourceServiceImplTests extends BaseTest {

    @Mock
    private ResourceRepository repository;

    private ResourceServiceImpl service;

    @Before
    public void before() throws Exception {
        service = new ResourceServiceImpl();
        service.setRepository(repository);
    }

    @Test
    public void persist_persistsUsagePoint() {
        UsagePoint usagePoint = newUsagePoint();

        service.persist(usagePoint);

        verify(repository).persist(usagePoint);
    }

    @Test
    public void findByRelatedHref_givenUsagePoint() {
        UsagePoint usagePoint = newUsagePoint();

        assertThat(service.findByAllParentsHref("href", usagePoint), is(empty()));
    }

    @Test
    public void findByRelatedHref_givenMeterReading() {
        MeterReading meterReading = newMeterReading();

        service.findByAllParentsHref("href", meterReading);

        verify(repository).findAllParentsByRelatedHref("href", meterReading);
    }

    @Test
    public void findAllRelated() {
        UsagePoint usagePoint = newUsagePoint();

        service.findAllRelated(usagePoint);

        verify(repository).findAllRelated(usagePoint);
    }

    @Test
    public void findByUUID() {
        UsagePoint usagePoint = newUsagePoint();

        service.findByUUID(usagePoint.getUUID(), usagePoint.getClass());

        verify(repository).findByUUID(usagePoint.getUUID(), usagePoint.getClass());
    }

    @Test
    public void findById() throws Exception {
        UsagePoint usagePoint = newUsagePoint();

        service.findById(usagePoint.getId(), usagePoint.getClass());

        verify(repository).findById(usagePoint.getId(), usagePoint.getClass());
    }
}
