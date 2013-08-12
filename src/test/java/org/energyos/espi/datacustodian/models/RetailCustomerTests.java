package org.energyos.espi.datacustodian.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class RetailCustomerTests {

    @Autowired
    private Validator validator;
    private RetailCustomer customer;
    private Errors errors;

    @Before
    public void setUp() {
        customer = new RetailCustomer();
        errors = new BeanPropertyBindingResult(customer, "customer");
    }

    @Test
    public void shouldBeValid_ifFieldsAreValid() throws Exception {
        customer.setFirstName("First");
        customer.setLastName("Last");

        validator.validate(customer, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void shouldNotBeValid_whenFirstNameMissing() throws Exception {
        customer.setLastName("bob");
        validator.validate(customer, errors);
        assertTrue(errors.hasErrors());
    }

    @Test
    public void shouldNotBeValid_whenLastNameMissing() throws Exception {
        customer.setFirstName("bob");
        validator.validate(customer, errors);
        assertTrue(errors.hasErrors());
    }

    @Test
    public void shouldNotBeValid_whenFirstNameTooLong() throws Exception {
        customer.setFirstName("abcdefghgijklmniopqrstuvwxyzlkjasdflkjasdlkfjasdlkfjasdflkj");
        customer.setLastName("last");
        validator.validate(customer, errors);
        assertTrue(errors.hasErrors());
    }

    @Test
    public void shouldNotBeValid_whenLastNameTooLong() throws Exception {
        customer.setFirstName("first");
        customer.setLastName("abcdefghgijklmniopqrstuvwxyzlkjasdflkjasdlkfjasdlkfjasdflkj");
        validator.validate(customer, errors);
        assertTrue(errors.hasErrors());
    }

}
