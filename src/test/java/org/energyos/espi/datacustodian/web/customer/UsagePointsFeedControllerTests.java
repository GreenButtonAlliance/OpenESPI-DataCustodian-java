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

package org.energyos.espi.datacustodian.web.customer;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.security.Principal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UsagePointsFeedControllerTests {

    @Test
    public void index_returnsAtomFeedOfUsagePointsForCurrentUser() throws Exception {
        UsagePointService usagePointService = mock(UsagePointService.class);

        UsagePointFeedController controller = new UsagePointFeedController();
        controller.setUsagePointService(usagePointService);


        String atomFeedResult = "<?xml version=\"1.0\"?><feed></feed>";

        when(usagePointService.exportUsagePoints(any(RetailCustomer.class))).thenReturn(atomFeedResult);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mock(RetailCustomer.class));

        assertEquals(atomFeedResult, controller.index(auth));
        verify(usagePointService).exportUsagePoints(any(RetailCustomer.class));
    }
}
