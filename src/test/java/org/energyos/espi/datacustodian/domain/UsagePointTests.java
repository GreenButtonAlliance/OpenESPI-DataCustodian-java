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

import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;
import static org.junit.Assert.assertTrue;

public class UsagePointTests {

    @Test
    public void title_should_beValid() throws Exception {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setDescription("Usage point title");
        usagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.ELECTRICITY_SERVICE));

        Set<ConstraintViolation<UsagePoint>> violations = validator.validate(usagePoint);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void validations() {
        assertAnnotationPresent(UsagePoint.class, "serviceCategory", NotNull.class);
    }
}
