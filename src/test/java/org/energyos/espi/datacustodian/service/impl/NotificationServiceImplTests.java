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

import org.energyos.espi.common.domain.BatchList;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.datacustodian.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

import static org.energyos.espi.common.test.EspiFactory.newSubscription;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class NotificationServiceImplTests extends BaseTest {

    @Mock
    public RestTemplate template;

    public NotificationServiceImpl service;

    @Before
    public void setup() {
        service = new NotificationServiceImpl();
        service.setRestTemplate(template);
    }

    @Test
    public void notify_notifiesThirdParty() {
        Subscription subscription = newSubscription();

        service.notify(subscription);

        verify(template).postForLocation(eq(subscription.getApplicationInformation().getThirdPartyDefaultNotifyResource()), any(BatchList.class));
    }
}
