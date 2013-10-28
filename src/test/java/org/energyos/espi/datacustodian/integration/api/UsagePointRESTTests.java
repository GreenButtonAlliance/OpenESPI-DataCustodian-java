package org.energyos.espi.datacustodian.integration.api;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.Routes;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.repositories.UsagePointRepository;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.utils.TestUtils;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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
    private UsagePointService usagePointService;

    @Autowired
    private UsagePointRepository usagePointRepository;

    @Autowired
    private RetailCustomerService retailCustomerService;

    public RetailCustomer retailCustomer;

    public UsagePoint usagePoint;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(retailCustomer);
        usagePoint = createUsagePoint();
    }

    @Test
    public void show_returnsOkStatus() throws Exception {
        mockMvc.perform(get(Routes.newDataCustodianRESTUsagePointMember(retailCustomer.getHashedId(), usagePoint.getHashedId())))
                .andExpect(status().isOk());
    }

    @Test
    public void show_returnsATOMContentType() throws Exception {
        mockMvc.perform(get(Routes.newDataCustodianRESTUsagePointMember(retailCustomer.getHashedId(), usagePoint.getHashedId())))
                .andExpect(content().contentType(MediaType.APPLICATION_ATOM_XML));
    }

    @Test
    public void show_returnsUsagePointXML() throws Exception {
        mockMvc.perform(get(Routes.newDataCustodianRESTUsagePointMember(retailCustomer.getHashedId(), usagePoint.getHashedId())))
                .andExpect(xpath("/:entry/:content/espi:UsagePoint", TestUtils.namespaces()).exists());
    }

    @Test
    public void index_returnsOkStatus() throws Exception {
        mockMvc.perform(get(Routes.newDataCustodianRESTUsagePointCollection(retailCustomer.getHashedId())))
                .andExpect(status().isOk());
    }

    @Test
    public void index_returnsATOMContentType() throws Exception {
        mockMvc.perform(get(Routes.newDataCustodianRESTUsagePointCollection(retailCustomer.getHashedId())))
                .andExpect(content().contentType(MediaType.APPLICATION_ATOM_XML));
    }

    @Test
    public void index_returnsUsagePointListXML() throws Exception {
        mockMvc.perform(get(Routes.newDataCustodianRESTUsagePointCollection(retailCustomer.getHashedId())))
                .andExpect(xpath("/:feed/:entry/:content/espi:UsagePoint", TestUtils.namespaces()).exists());
    }

    public void create_createsUsagePoint() throws Exception {
        int beforeCount = usagePointRepository.findAllByRetailCustomerId(1L).size();

        mockMvc.perform(post("/espi/1_1/resource/RetailCustomer/1/UsagePoint").contentType(MediaType.APPLICATION_ATOM_XML)
                .content("<entry xmlns=\"http://www.w3.org/2005/Atom\">" +
                "  <id>urn:uuid:97EAEBAD-1214-4A58-A3D4-A16A6DE718E1</id>" +
                "  <published>2012-10-24T00:00:00Z</published>" +
                "  <updated>2012-10-24T00:00:00Z</updated>" +
                "  <link rel=\"self\"" +
                "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01\"/>" +
                "  <link rel=\"up\"" +
                "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint\"/>" +
                "  <link rel=\"related\"" +
                "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01/MeterReading\"/>" +
                "  <link rel=\"related\"" +
                "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01/ElectricPowerUsageSummary\"/>" +
                "  <link rel=\"related\"" +
                "        href=\"/espi/1_1/resource/UsagePoint/01/LocalTimeParameters/01\"/>" +
                "  <title>my house</title>" +
                "  <content>" +
                "    <UsagePoint xmlns=\"http://naesb.org/espi\">" +
                "      <ServiceCategory>" +
                "        <kind>0</kind>" +
                "      </ServiceCategory>" +
                "    </UsagePoint>" +
                "  </content>" +
                "</entry>"))
                .andExpect(status().isOk());

        assertThat(usagePointRepository.findAllByRetailCustomerId(1L).size(), is(beforeCount + 1));
    }

    @Test
    public void create_withInvalidUsagePoint_returns405() throws Exception {
        mockMvc.perform(post("/espi/1_1/resource/RetailCustomer/1/UsagePoint").contentType(MediaType.APPLICATION_ATOM_XML)
                .content("<entry xmlns=\"http://www.w3.org/2005/Atom\">" +
                        "  <id>urn:uuid:97EAEBAD-1214-4A58-A3D4-A16A6DE718E1</id>" +
                        "  <published>2012-10-24T00:00:00Z</published>" +
                        "  <updated>2012-10-24T00:00:00Z</updated>" +
                        "  <link rel=\"self\"" +
                        "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01\"/>" +
                        "  <link rel=\"up\"" +
                        "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint\"/>" +
                        "  <link rel=\"related\"" +
                        "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01/MeterReading\"/>" +
                        "  <link rel=\"related\"" +
                        "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01/ElectricPowerUsageSummary\"/>" +
                        "  <link rel=\"related\"" +
                        "        href=\"/espi/1_1/resource/UsagePoint/01/LocalTimeParameters/01\"/>" +
                        "  <title>my house</title>" +
                        "  <content>" +
                        "    <MeterReading xmlns=\"http://naesb.org/espi\">" +
                        "      <ServiceCategory>" +
                        "        <kind>0</kind>" +
                        "      </ServiceCategory>" +
                        "    </MeterReading>" +
                        "  </content>" +
                        "</entry>"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void isOneLevelDeep() throws Exception {
        mockMvc.perform(get(Routes.newDataCustodianRESTUsagePointCollection(retailCustomer.getHashedId())))
                .andExpect(xpath("/:feed/:entry/:content/espi:UsagePoint", TestUtils.namespaces()).exists())
                .andExpect(xpath("//espi:ElectricPowerQualitySummary", TestUtils.namespaces()).doesNotExist());
    }

    private UsagePoint createUsagePoint() {
        UsagePoint usagePoint = EspiFactory.newUsagePoint(retailCustomer);
        usagePointService.persist(usagePoint);
        return usagePoint;
    }
}
