package org.energyos.espi.datacustodian.web.customer;

import org.energyos.espi.datacustodian.service.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/RetailCustomer/{retailCustomerId}/ThirdPartyList")
public class ThirdPartyController {

    @Autowired
    private ThirdPartyService thirdPartyService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(ModelMap model) {
        model.put("thirdParties", thirdPartyService.findAll());
        return "/customer/thirdparties/index";
    }

    public void setThirdPartyService(ThirdPartyService thirdPartyService) {
        this.thirdPartyService = thirdPartyService;
    }
}
