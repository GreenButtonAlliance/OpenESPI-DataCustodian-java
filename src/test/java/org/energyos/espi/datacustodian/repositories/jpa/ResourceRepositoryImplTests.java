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


import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.LinkType;
import org.energyos.espi.datacustodian.repositories.ResourceRepository;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ResourceRepositoryImplTests {

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
        LinkType relatedLink = new LinkType();
        relatedLink.setRel("related");
        relatedLink.setHref("href");
        usagePoint.getRelatedLinks().add(relatedLink);

        repository.persist(usagePoint);

        assertThat(repository.findByRelatedHref("href", usagePoint).getId(), equalTo(usagePoint.getId()));
    }
}
