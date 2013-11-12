package org.energyos.espi.datacustodian.integration.api;

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.repositories.UsagePointRepository;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.test.EspiFactory;
import org.energyos.espi.common.test.EspiPersistenceFactory;
import org.energyos.espi.common.test.TestUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class UsagePointRESTTests {

    public RetailCustomer retailCustomer;
    public UsagePoint usagePoint;
    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    protected EspiPersistenceFactory factory;
    private MockMvc mockMvc;
    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private UsagePointRepository usagePointRepository;
    @Autowired
    private RetailCustomerService retailCustomerService;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        usagePoint = factory.createUsagePoint();
        retailCustomer = usagePoint.getRetailCustomer();
    }

    @Test
    public void show_returnsOkStatus() throws Exception {
        mockMvc.perform(get(Routes.buildDataCustodianRESTUsagePointMember(retailCustomer.getHashedId(), usagePoint.getHashedId())))
                .andExpect(status().isOk());
    }

    @Test
    public void show_returnsATOMContentType() throws Exception {
        mockMvc.perform(get(Routes.buildDataCustodianRESTUsagePointMember(retailCustomer.getHashedId(), usagePoint.getHashedId())))
                .andExpect(content().contentType(MediaType.APPLICATION_ATOM_XML));
    }

    @Test
    public void show_returnsUsagePointXML() throws Exception {
        mockMvc.perform(get(Routes.buildDataCustodianRESTUsagePointMember(retailCustomer.getHashedId(), usagePoint.getHashedId())))
                .andExpect(MockMvcResultMatchers.xpath("/:entry/:content/espi:UsagePoint", TestUtils.namespaces()).exists())
                .andExpect(MockMvcResultMatchers.xpath("/:entry/:link[@rel='self']", TestUtils.namespaces()).exists());

    }

    @Test
    public void show_givenAnInvalidUsagePoint_returns400Status() throws Exception {
        mockMvc.perform(get(Routes.buildDataCustodianRESTUsagePointMember(retailCustomer.getHashedId(), String.valueOf(System.nanoTime()))))
                .andExpect(status().is(400));
    }

    @Test
    public void index_returnsOkStatus() throws Exception {
        mockMvc.perform(get(Routes.buildDataCustodianRESTUsagePointCollection(retailCustomer.getHashedId())))
                .andExpect(status().isOk());
    }

    @Test
    public void index_returnsATOMContentType() throws Exception {
        mockMvc.perform(get(Routes.buildDataCustodianRESTUsagePointCollection(retailCustomer.getHashedId())))
                .andExpect(content().contentType(MediaType.APPLICATION_ATOM_XML));
    }

    @Test
    public void index_returnsUsagePointListXML() throws Exception {
        mockMvc.perform(get(Routes.buildDataCustodianRESTUsagePointCollection(retailCustomer.getHashedId())))
                .andExpect(MockMvcResultMatchers.xpath("/:feed/:entry/:content/espi:UsagePoint", TestUtils.namespaces()).exists())
                .andExpect(MockMvcResultMatchers.xpath("/:feed/:entry/:link[@rel='self']", TestUtils.namespaces()).exists());
    }

    @Test
    public void create_createsAndReturnsOKForValidUsagePoint() throws Exception {
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
    public void create_returnsMethodNotAllowedForInvalidUsagePoint() throws Exception {
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
    public void update_updatesAndReturnsOKForValidUsagePoint() throws Exception {
        UsagePoint usagePoint = EspiFactory.newUsagePoint(retailCustomer);
        usagePointService.persist(usagePoint);

        mockMvc.perform(put("/espi/1_1/resource/RetailCustomer/" + retailCustomer.getId() + "/UsagePoint/" + usagePoint.getHashedId()).contentType(MediaType.APPLICATION_ATOM_XML)
                .content("<entry xmlns=\"http://www.w3.org/2005/Atom\">" +
                        "  <id>urn:uuid:" + usagePoint.getUUID() + "</id>" +
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
                        "  <title>Our House</title>" +
                        "  <content>" +
                        "    <UsagePoint xmlns=\"http://naesb.org/espi\">" +
                        "      <ServiceCategory>" +
                        "        <kind>0</kind>" +
                        "      </ServiceCategory>" +
                        "    </UsagePoint>" +
                        "  </content>" +
                        "</entry>"))
                .andExpect(status().isOk());

        assertThat(usagePointService.findByHashedId(usagePoint.getHashedId()).getDescription(), equalTo("Our House"));
    }

    @Test
    public void update_returnsBadRequestForInvalidUsagePointId() throws Exception {
        RetailCustomer otherCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(otherCustomer);
        UsagePoint otherUsagePoint = EspiFactory.newUsagePoint(otherCustomer);
        usagePointService.persist(otherUsagePoint);

        mockMvc.perform(put("/espi/1_1/resource/RetailCustomer/" + retailCustomer.getId() + "/UsagePoint/" + otherUsagePoint.getHashedId()).contentType(MediaType.APPLICATION_ATOM_XML)
                .content("<entry xmlns=\"http://www.w3.org/2005/Atom\">" +
                        "  <id>urn:uuid:" + otherUsagePoint.getUUID() + "</id>" +
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
                        "  <title>Our House</title>" +
                        "  <content>" +
                        "    <UsagePoint xmlns=\"http://naesb.org/espi\">" +
                        "      <ServiceCategory>" +
                        "        <kind>0</kind>" +
                        "      </ServiceCategory>" +
                        "    </UsagePoint>" +
                        "  </content>" +
                        "</entry>"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Ignore("Consistently fails only on GreenButton VM for unknown reasons. Ignoring for now.")
    public void update_returnsBadRequestForUnknownUsagePointId() throws Exception {
        mockMvc.perform(put("/espi/1_1/resource/RetailCustomer/" + retailCustomer.getId() + "/UsagePoint/42").contentType(MediaType.APPLICATION_ATOM_XML)
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
                        "  <title>Our House</title>" +
                        "  <content>" +
                        "    <UsagePoint xmlns=\"http://naesb.org/espi\">" +
                        "      <ServiceCategory>" +
                        "        <kind>0</kind>" +
                        "      </ServiceCategory>" +
                        "    </UsagePoint>" +
                        "  </content>" +
                        "</entry>"))
                .andExpect(status().is(400));
    }

    @Test
    public void update_returnsMethodNotAllowedForInvalidUsagePointId() throws Exception {
        UsagePoint usagePoint = EspiFactory.newUsagePoint(retailCustomer);
        usagePointService.persist(usagePoint);

        mockMvc.perform(put("/espi/1_1/resource/RetailCustomer/" + retailCustomer.getId() + "/UsagePoint/" + usagePoint.getHashedId()).contentType(MediaType.APPLICATION_ATOM_XML)
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
                        "  <title>Invalid</title>" +
                        "  <content>" +
                        "    <UsagePoint xmlns=\"http://naesb.org/espi\">" +
                        "      <ServiceCategory>" +
                        "      </ServiceCategory>" +
                        "    </UsagePoint>" +
                        "  </content>" +
                        "</entry>"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void delete_returnsOK_forValidUsagePoint() throws Exception {
        UsagePoint usagePoint = EspiFactory.newUsagePoint(retailCustomer);
        usagePointService.persist(usagePoint);

        mockMvc.perform(delete("/espi/1_1/resource/RetailCustomer/" + retailCustomer.getId() + "/UsagePoint/" + usagePoint.getHashedId()))
                .andExpect(status().isOk());

        usagePointService.findByHashedId(usagePoint.getHashedId());
    }

    @Test
    public void delete_returns400_forMissingUsagePoint() throws Exception {
        mockMvc.perform(delete("/espi/1_1/resource/RetailCustomer/1/UsagePoint/12345"))
                .andExpect(status().is(400));
    }

    @Test
    public void delete_returns400_forBadId() throws Exception {
        RetailCustomer otherCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(otherCustomer);
        UsagePoint otherUsagePoint = EspiFactory.newUsagePoint(otherCustomer);
        usagePointService.persist(otherUsagePoint);

        mockMvc.perform(delete("/espi/1_1/resource/RetailCustomer/" + retailCustomer.getHashedId() + "/UsagePoint/" + otherUsagePoint.getHashedId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void isOneLevelDeep() throws Exception {
        mockMvc.perform(get(Routes.buildDataCustodianRESTUsagePointCollection(retailCustomer.getHashedId())))
                .andExpect(MockMvcResultMatchers.xpath("/:feed/:entry/:content/espi:UsagePoint", TestUtils.namespaces()).exists())
                .andExpect(MockMvcResultMatchers.xpath("//espi:ElectricPowerQualitySummary", TestUtils.namespaces()).doesNotExist());
    }
}
