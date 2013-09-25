package org.energyos.espi.datacustodian.repositories.jpa;

import org.energyos.espi.datacustodian.repositories.ThirdPartyRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ThirdPartyRepositoryImplTest {

    @Resource
    private ThirdPartyRepository repository;

    @Test
    public void findAll_returnsAllThirdParties() throws Exception {
        assertTrue("Repository has no data", repository.findAll().size() > 0);
    }
}
