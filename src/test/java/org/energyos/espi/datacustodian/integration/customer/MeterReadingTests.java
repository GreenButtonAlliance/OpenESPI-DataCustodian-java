package org.energyos.espi.datacustodian.integration.customer;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.energyos.espi.common.domain.MeterReading;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.MeterReadingService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.test.EspiFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class MeterReadingTests {
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    protected RetailCustomerService retailCustomerService;
    
    @Autowired
    protected UsagePointService usagePointService;
    
    @Autowired
    protected MeterReadingService meterReadingService;

    protected TestingAuthenticationToken authentication;
    private MeterReading meterReading;
    private String showPath;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        RetailCustomer customer = retailCustomerService.findById(1L);
       
        UsagePoint usagePoint = EspiFactory.newUsagePoint();
        usagePointService.persist(usagePoint);
        
        authentication = new TestingAuthenticationToken(customer, null);
        
        meterReading = EspiFactory.newMeterReading();
        meterReadingService.persist(meterReading);
        showPath = "/RetailCustomer/" + customer.getId() + "/UsagePoint/" + usagePoint.getId() + "/MeterReading/" + meterReading.getId() + "/show";
    }

    @Test
    public void show_returnsOkStatus() throws Exception {
        mockMvc.perform(get(showPath).principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    public void show_displaysShowView() throws Exception {
        mockMvc.perform(get(showPath).principal(authentication))
                .andExpect(view().name("/customer/meterreadings/show"));
    }

    @Test
    public void show_setsCurrentCustomerModel() throws Exception {
        mockMvc.perform(get(showPath).principal(authentication))
                .andExpect(model().attributeExists("currentCustomer"));
    }

    @Test
    public void show_setsMeterReadingModel() throws Exception {
        mockMvc.perform(get(showPath).principal(authentication))
                .andExpect(model().attributeExists("meterReading"));
    }
}

