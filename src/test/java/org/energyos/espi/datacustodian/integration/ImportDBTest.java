package org.energyos.espi.datacustodian.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.energyos.espi.common.service.RetailCustomerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional (rollbackFor= {javax.xml.bind.JAXBException.class}, 
                noRollbackFor = {javax.persistence.NoResultException.class, org.springframework.dao.EmptyResultDataAccessException.class })

public class ImportDBTest {
    @Autowired
    private RetailCustomerService retailCustomerService;

    @Test
    public void populateDB() throws Exception {
        assertThat(retailCustomerService.findAll().size(), equalTo(8));
        assertThat(retailCustomerService.findById(1L).getFirstName(), equalTo("Alan"));
    }
}
