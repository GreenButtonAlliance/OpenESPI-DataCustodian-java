package org.energyos.espi.datacustodian.web.api.impl;
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

import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.energyos.espi.datacustodian.web.api.FeedBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.Result;
import java.util.LinkedList;

import static org.mockito.Mockito.*;

public class AtomServiceImplTest {

    @Mock
    Jaxb2Marshaller marshaller;

    @Mock
    FeedBuilder feedBuilder;
    private AtomServiceImpl atomService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        atomService = new AtomServiceImpl();
        atomService.setFeedBuilder(feedBuilder);
        atomService.setMarshaller(marshaller);
    }

    @Test
    public void feedFor() throws Exception {
        FeedType feed = new FeedType();
        LinkedList<UsagePoint> usagePointList = new LinkedList<>();

        when(feedBuilder.build(usagePointList)).thenReturn(feed);

        atomService.feedFor(usagePointList);

        verify(marshaller).marshal(eq(feed), any(Result.class));
    }
}
