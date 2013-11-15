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

package org.energyos.espi.datacustodian.service.impl;


import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.repositories.ResourceRepository;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ResourceServiceImplTests {

    @Test
    public void persist_persistsUsagePoint() {
        ResourceServiceImpl service = new ResourceServiceImpl();
        ResourceRepository repository = mock(ResourceRepository.class);
        service.setRepository(repository);
        UsagePoint usagePoint = new UsagePoint();

        service.persist(usagePoint);

        verify(repository).persist(usagePoint);
    }

    @Test
    public void findByRelatedHref_findsRelatedResource() {
        ResourceServiceImpl service = new ResourceServiceImpl();
        ResourceRepository repository = mock(ResourceRepository.class);
        service.setRepository(repository);
        UsagePoint usagePoint = mock(UsagePoint.class);

        service.findByRelatedHref("href", usagePoint);

        verify(repository).findByRelatedHref("href", usagePoint);
    }

    @Test
    public void findAllRelated() {
        ResourceServiceImpl service = new ResourceServiceImpl();
        ResourceRepository repository = mock(ResourceRepository.class);
        service.setRepository(repository);
        UsagePoint usagePoint = mock(UsagePoint.class);

        service.findAllRelated(usagePoint);

        verify(repository).findAllRelated(usagePoint);
    }

    @Test
    public void findByUUID() {
        ResourceServiceImpl service = new ResourceServiceImpl();
        ResourceRepository repository = mock(ResourceRepository.class);
        service.setRepository(repository);
        UsagePoint usagePoint = mock(UsagePoint.class);

        service.findByUUID(usagePoint.getUUID(), usagePoint.getClass());

        verify(repository).findByUUID(usagePoint.getUUID(), usagePoint.getClass());
    }
}
