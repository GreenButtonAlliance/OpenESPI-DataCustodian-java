package org.energyos.espi.datacustodian.web.custodian;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

public class RetailCustomerUsagePointControllerTests {
    private RetailCustomerUsagePointController controller;

    @Before
    public void before() {
        controller = new RetailCustomerUsagePointController();
    }
    @Test
    public void form_displaysFormView() {
        assertEquals("/custodian/retailcustomers/usagepoints/form", controller.form(1L, new ModelMap()));
    }

    @Test
    public void form_setsUsagePointModel() {
        ModelMap model = new ModelMap();

        controller.form(1L, model);

        assertEquals(RetailCustomerUsagePointController.UsagePointForm.class, model.get("usagePointForm").getClass());
    }

    @Test
    public void create_givenValidInput_redirectsToRetailCustomers() {
        RetailCustomerService retailCustomerService = mock(RetailCustomerService.class);
        UsagePointService service = mock(UsagePointService.class);

        when(retailCustomerService.findById(anyLong())).thenReturn(new RetailCustomer());

        controller.setRetailCustomerService(retailCustomerService);
        controller.setService(service);

        RetailCustomerUsagePointController.UsagePointForm usagePointForm = new RetailCustomerUsagePointController.UsagePointForm();
        usagePointForm.setUUID(UUID.randomUUID().toString());
        usagePointForm.setDescription("Front Electric Meter");

        assertEquals("redirect:/custodian/retailcustomers", controller.create(1L, usagePointForm, mock(BindingResult.class)));
    }

    @Test
    public void create_givenValidInput_persistsUsagePoint() {
        RetailCustomerService retailCustomerService = mock(RetailCustomerService.class);
        UsagePointService service = mock(UsagePointService.class);
        RetailCustomerUsagePointController.UsagePointForm usagePointForm = new RetailCustomerUsagePointController.UsagePointForm();
        usagePointForm.setUUID(UUID.randomUUID().toString());
        usagePointForm.setDescription("Front Electric Meter");

        when(retailCustomerService.findById(anyLong())).thenReturn(new RetailCustomer());
        controller.setRetailCustomerService(retailCustomerService);
        controller.setService(service);

        controller.create(1L, usagePointForm, mock(BindingResult.class));

        verify(service).createOrReplaceByUUID(any(UsagePoint.class));
    }

    @Test
    public void create_givenInValidInput_displaysFormView() {
        RetailCustomerUsagePointController.UsagePointForm usagePointForm = new RetailCustomerUsagePointController.UsagePointForm();

        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        assertEquals("/custodian/retailcustomers/usagepoints/form", controller.create(1L, usagePointForm, result));
    }
}
