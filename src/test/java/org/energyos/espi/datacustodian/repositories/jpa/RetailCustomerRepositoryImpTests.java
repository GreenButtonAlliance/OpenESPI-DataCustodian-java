package org.energyos.espi.datacustodian.repositories.jpa;

import org.energyos.espi.datacustodian.repositories.RetailCustomerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class RetailCustomerRepositoryImpTests {

    @Resource(name = "retailCustomerRepositoryJpa")
    private RetailCustomerRepository repository;

    @Test
    public void findAll_shouldReturnAllRetailCustomers() throws Exception {
        assertTrue(repository.findAll().size() == 7);
    }
}
