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


import static org.energyos.espi.common.test.EspiFactory.newUsagePoint;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.energyos.espi.common.domain.BatchList;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.datacustodian.BaseTest;
import org.junit.Test;

public class UpdateServiceImplTests extends BaseTest {
    @Test
    public void updatedResources() {
        UpdateServiceImpl service = new UpdateServiceImpl();
        UsagePointService usagePointService = mock(UsagePointService.class);
        service.setUsagePointService(usagePointService);
        UsagePoint resource = newUsagePoint();
        List<UsagePoint> usagePoints = new ArrayList<>();
        usagePoints.add(resource);
        Subscription subscription = new Subscription();
        when(usagePointService.findAllUpdatedFor(subscription)).thenReturn(usagePoints);

        BatchList batchList = service.updatedResources(subscription);

        assertEquals(resource.getSelfHref(), batchList.getResources().get(0));
    }
}
