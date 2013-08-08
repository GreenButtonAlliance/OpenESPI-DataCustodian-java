package org.energyos.espi.datacustodian.controllers;

import org.energyos.espi.datacustodian.repositories.RetailCustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

@Controller
@RequestMapping("/retailcustomers")
public class RetailCustomersController {

    @Resource(name="retailCustomerRepository")
    private RetailCustomerRepository customerRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String index(ModelMap model) {
        model.put("customers", customerRepository.findAll());

        return "retailcustomers/index";
    }
}