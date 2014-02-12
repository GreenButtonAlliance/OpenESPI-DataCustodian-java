package org.energyos.espi.datacustodian.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.energyos.espi.common.domain.RetailCustomer;
import org.junit.Test;
import org.springframework.security.core.Authentication;

public class DefaultControllerTest {

    @Test
    public void whenRoleUser_redirectsToRetailCustomerHome() {
        DefaultController controller = new DefaultController();
        RetailCustomer customer = new RetailCustomer();
        customer.setId(99L);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.isUserInRole(RetailCustomer.ROLE_USER)).thenReturn(true);
        when(request.isUserInRole(RetailCustomer.ROLE_CUSTODIAN)).thenReturn(false);

        Authentication principal = mock(Authentication.class);
        when(principal.getPrincipal()).thenReturn(customer);

        assertEquals("redirect:/RetailCustomer/" + customer.getId() + "/home",
                controller.defaultAfterLogin(request, principal));
    }
}
