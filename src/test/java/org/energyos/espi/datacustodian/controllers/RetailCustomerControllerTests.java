package org.energyos.espi.datacustodian.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.ui.ModelMap;

import java.util.List;

import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class RetailCustomerControllerTests {

    @Autowired
    protected RetailCustomersController controller;

    @Test
    public void shouldGetListOfCustomers() throws Exception {
        ModelMap model = new ModelMap();
        controller.index(model);

        assertTrue(((List)model.get("customers")).size() == 1);
    }
}
