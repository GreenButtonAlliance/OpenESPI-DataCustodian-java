package org.energyos.espi.datacustodian.integration.web.filters;


import org.energyos.espi.datacustodian.web.filter.OriginFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Profile("test")
public class OriginFilterTest {

    @Autowired
    protected OriginFilter filter;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac)
                .addFilters(filter).build();
    }

    @Test
    public void optionsRequest_hasCorrectFilters() throws Exception {
        mockMvc.perform(options("/"))
                .andExpect(header().string("Access-Control-Allow-Origin", is("*")))
                .andExpect(header().string("Access-Control-Allow-Methods", is("GET, POST, PUT, DELETE")))
                .andExpect(header().string("Access-Control-Allow-Headers", is("Authorization")))
                .andExpect(header().string("Access-Control-Max-Age", is("1800")));
    }
}
