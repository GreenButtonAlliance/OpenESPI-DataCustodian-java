package org.energyos.espi.datacustodian.web;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

public class BaseController {
    @ModelAttribute("currentCustomer")
    public RetailCustomer currentCustomer(Principal principal) {
        return (RetailCustomer) ((Authentication)principal).getPrincipal();
    }
}
