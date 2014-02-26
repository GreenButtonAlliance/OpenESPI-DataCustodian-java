package org.energyos.espi.datacustodian.web.api;

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

import static org.energyos.espi.common.test.EspiFactory.newUsagePoint;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.test.EspiFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.mock.web.MockHttpServletResponse;

import com.sun.syndication.io.FeedException;

public class UsagePointRESTControllerTests {
    private MockHttpServletResponse response;
    @Mock
    private UsagePointService usagePointService;
    @Mock
    private RetailCustomerService retailCustomerService;
    @Mock
    private UsagePointRESTController controller;
    private RetailCustomer retailCustomer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        response = new MockHttpServletResponse();

        controller = new UsagePointRESTController();
        controller.setRetailCustomerService(retailCustomerService);
        controller.setUsagePointService(usagePointService);
        // controller.setAtomService(atomService);

        retailCustomer = new RetailCustomer();
    }

    @Test
    @Ignore("TODO Untill Things Stablize")
    public void index() throws IOException, FeedException {
        String feed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><feed></feed>";

        List<UsagePoint> usagePointList = new ArrayList<>();
        usagePointList.add(newUsagePoint());

        when(retailCustomerService.findById(1L)).thenReturn(retailCustomer);
        when(usagePointService.findAllByRetailCustomer(retailCustomer)).thenReturn(usagePointList);

        controller.index(response, 1L, null);

        assertThat(response.getContentAsString(), is(feed));
        assertThat(response.getStatus(), is(200));
    }

    @Test
    @Ignore("TODO Untill Things Stablize")
    public void show() throws IOException, FeedException {
        String entry = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><entry></entry>";

        UsagePoint usagePoint = EspiFactory.newUsagePoint();

        when(usagePointService.findByHashedId(usagePoint.getHashedId())).thenReturn(usagePoint);

        controller.show(response, 1L, null);

        assertThat(response.getContentAsString(), is(entry));
        assertThat(response.getStatus(), is(200));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    @Ignore("TODO Untill Things Stablize")
    public void show_givenInvalidUsagePoint_returns400Status() throws IOException, FeedException {
        when(usagePointService.findByHashedId(anyString())).thenThrow(new EmptyResultDataAccessException(1));

        controller.show(response, 100L, null);
    }

    @Test
    @Ignore("TODO Untill Things Stablize")
    public void create() throws IOException {
        InputStream inputStream = mock(InputStream.class);
        UsagePoint usagePoint = newUsagePoint();

        when(retailCustomerService.findById(1L)).thenReturn(retailCustomer);
        when(usagePointService.importResource(inputStream)).thenReturn(usagePoint);

        controller.create(response, 1L, null, inputStream);

        verify(usagePointService).importResource(inputStream);
        verify(usagePointService).associateByUUID(retailCustomer, usagePoint.getUUID());
    }

    @Test
    @Ignore("TODO Untill Things Stablize")
    public void update() {
        InputStream inputStream = mock(InputStream.class);
        UsagePoint usagePoint = mock(UsagePoint.class);

        when(usagePoint.getRetailCustomer()).thenReturn(retailCustomer);
        when(retailCustomerService.findById(1L)).thenReturn(retailCustomer);
        when(usagePointService.findByHashedId("1")).thenReturn(usagePoint);
        when(usagePointService.importResource(inputStream)).thenReturn(usagePoint);

        controller.update(response, 1L, 1L, null, inputStream);

        verify(usagePointService).importResource(inputStream);
        verify(usagePointService).associateByUUID(retailCustomer, usagePoint.getUUID());
    }

    @Test
    @Ignore("TODO Untill Things Stablize")
    public void delete() {
        String hashedId = "1";
        UsagePoint usagePoint = mock(UsagePoint.class);

        when(usagePoint.getRetailCustomer()).thenReturn(retailCustomer);
        when(retailCustomerService.findById(1L)).thenReturn(retailCustomer);
        when(usagePointService.findByHashedId(hashedId)).thenReturn(usagePoint);

        controller.delete(response, 1L, 1L, null);

        verify(usagePointService).deleteByHashedId(hashedId);
    }
}
