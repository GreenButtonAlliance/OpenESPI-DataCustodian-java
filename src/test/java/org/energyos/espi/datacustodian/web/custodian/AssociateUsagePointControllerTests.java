package org.energyos.espi.datacustodian.web.custodian;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.UsagePointService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional (rollbackFor= {javax.xml.bind.JAXBException.class}, 
                noRollbackFor = {javax.persistence.NoResultException.class, org.springframework.dao.EmptyResultDataAccessException.class })

public class AssociateUsagePointControllerTests {

    @Autowired
    protected AssociateUsagePointController controller;
    @Mock
    private RetailCustomerService retailCustomerService;
    @Mock
    private UsagePointService service;
    @Mock
    private BindingResult bindingResult;

    private ArgumentCaptor<UsagePoint> usagePointCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller.setService(service);
        controller.setRetailCustomerService(retailCustomerService);

        usagePointCaptor = ArgumentCaptor.forClass(UsagePoint.class);
    }

    @Test
    public void create_whenThereAreErrors_redisplaysTheForm() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);

        String route = controller.create(null, null, bindingResult);
        assertThat(route, is("/custodian/retailcustomers/usagepoints/form"));
    }

    @Test
    @Ignore
    public void create_associatesTheUsagePointWithTheRetailCustomer() {
        long retailCustomerId = 5;
        RetailCustomer retailCustomer = new RetailCustomer();
        when(retailCustomerService.findById(retailCustomerId)).thenReturn(retailCustomer);

        controller.create(retailCustomerId, newUsagePointForm(), bindingResult);

        verify(service).createOrReplaceByUUID(usagePointCaptor.capture());
        assertThat(usagePointCaptor.getValue().getRetailCustomer(), is(equalTo(retailCustomer)));
    }

    @Test
    public void create_redirectsToTheRetailCustomersIndex() {
        long retailCustomerId = 5;

        String route = controller.create(retailCustomerId, newUsagePointForm(), bindingResult);
        assertThat(route, is("redirect:" + "/custodian/retailcustomers"));
    }


    public AssociateUsagePointController.UsagePointForm newUsagePointForm() {
        AssociateUsagePointController.UsagePointForm usagePointForm = new AssociateUsagePointController.UsagePointForm();
        usagePointForm.setUUID("550e8400-e29b-41d4-a716-446655440000");
        return usagePointForm;
    }

}
