package org.energyos.espi.datacustodian.integration.api;

import org.energyos.espi.datacustodian.domain.ElectricPowerQualitySummary;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.repositories.RetailCustomerRepository;
import org.energyos.espi.datacustodian.repositories.UsagePointRepository;
import org.energyos.espi.datacustodian.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newRetailCustomer;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newUsagePoint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class UsagePointRESTTests {

    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    private RetailCustomerRepository retailCustomerRepository;

    @Autowired
    private UsagePointRepository usagePointRepository;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void retrieveAllUsagePoints() throws Exception {
        mockMvc.perform(get("/espi/1_1/resource/RetailCustomer/1/UsagePoint"))
            .andExpect(status().isOk())
            .andExpect(xpath("/:feed/:entry/:content/espi:UsagePoint", TestUtils.namespaces()).exists());
    }

    @Test
    public void isOneLevelDeep() throws Exception {
        RetailCustomer retailCustomer = newPersistedRetailCustomer();
        Long id = retailCustomer.getId();
        createUsagePoint(retailCustomer);


        mockMvc.perform(get("/espi/1_1/resource/RetailCustomer/" + id + "/UsagePoint"))
                .andExpect(status().isOk())
                .andExpect(xpath("/:feed/:entry/:content/espi:UsagePoint", TestUtils.namespaces()).exists())
                .andExpect(xpath("//espi:ElectricPowerQualitySummary", TestUtils.namespaces()).doesNotExist());
    }

    private RetailCustomer newPersistedRetailCustomer() {
        RetailCustomer retailCustomer = newRetailCustomer();
        retailCustomerRepository.persist(retailCustomer);
        return retailCustomer;
    }

    private void createUsagePoint(RetailCustomer retailCustomer) {
        UsagePoint usagePoint = newUsagePoint();
        ElectricPowerQualitySummary e = new ElectricPowerQualitySummary();
        e.setDescription("asdf");
        usagePoint.getElectricPowerQualitySummaries().add(e);
        usagePoint.setRetailCustomer(retailCustomer);

        usagePointRepository.persist(usagePoint);
    }
}
