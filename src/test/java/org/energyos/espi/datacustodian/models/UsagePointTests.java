/*
 * Copyright 2013 EnergyOS ESPI
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

import org.junit.Test;
import javax.validation.*;
import java.util.Set;

import static org.energyos.espi.datacustodian.support.TestUtils.*;
import static org.junit.Assert.assertTrue;

public class UsagePointTests {

    @Test
    public void title_should_beValid() throws Exception {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setTitle("Usage point title");

        Set<ConstraintViolation<UsagePoint>> violations = validator.validate(usagePoint);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void validations() {
        assertNotEmptyValidation(UsagePoint.class, "title");
        assertSizeValidation(UsagePoint.class, "title", 0, 100);
    }
}
