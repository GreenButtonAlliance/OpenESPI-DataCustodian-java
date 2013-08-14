/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
