package org.energyos.espi.datacustodian.integration.customer;


import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.service.MeterReadingService;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

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
    protected MeterReadingService meterReadingService;

    protected TestingAuthenticationToken authentication;
    private MeterReading meterReading;
    private String showPath;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        RetailCustomer customer = retailCustomerService.findById(1L);
        authentication = new TestingAuthenticationToken(customer, null);
        meterReading = new MeterReading();
        meterReadingService.persist(meterReading);
        showPath = "/RetailCustomer/" + customer.getId() + "/meterreadings/" + meterReading.getId() + "/show";
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

