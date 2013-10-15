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

package org.energyos.espi.datacustodian.domain;

import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SubscriptionTests {

    @Test
    public void isValid() throws Exception {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();

        Subscription subscription = EspiFactory.newSubscription(retailCustomer);

        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void isInvalid() throws Exception {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Subscription subscription = new Subscription();

        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void extendsIdentifiableObject() {
        assertTrue(Subscription.class.getSuperclass() == IdentifiedObject.class);
    }

    @Test
    public void persistence() {
        assertAnnotationPresent(Subscription.class, Entity.class);
        assertAnnotationPresent(Subscription.class, Table.class);
    }

    @Test
    public void retailCustomer() {
        assertAnnotationPresent(Subscription.class, "retailCustomer", ManyToOne.class);
        assertAnnotationPresent(Subscription.class, "retailCustomer", JoinColumn.class);
        assertAnnotationPresent(Subscription.class, "retailCustomer", NotNull.class);
    }
}