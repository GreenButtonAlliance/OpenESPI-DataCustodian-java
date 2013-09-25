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

package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.datacustodian.domain.ThirdParty;
import org.energyos.espi.datacustodian.repositories.ThirdPartyRepository;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThirdPartyServiceImplTests {

    @Test
    public void findAll() {
        ThirdPartyServiceImpl service = new ThirdPartyServiceImpl();
        ThirdPartyRepository repository = mock(ThirdPartyRepository.class);
        List<ThirdParty> thirdParties = new ArrayList<>();

        service.setRepository(repository);
        when(repository.findAll()).thenReturn(thirdParties);

        assertEquals(thirdParties, service.findAll());
    }
}
