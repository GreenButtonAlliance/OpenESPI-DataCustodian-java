package org.energyos.espi.datacustodian.integration;

import org.energyos.espi.common.service.ApplicationInformationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class PopulateDBTest {
    @Autowired
    private ApplicationInformationService applicationInformationService;

    @Test
    public void populateDB() throws Exception {
        assertThat(applicationInformationService.findAll().size(), equalTo(1));
    }
}
