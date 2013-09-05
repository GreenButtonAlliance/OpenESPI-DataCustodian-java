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

package org.energyos.espi.datacustodian.service;


import org.energyos.espi.datacustodian.repositories.IntervalBlockRepository;
import org.energyos.espi.datacustodian.service.impl.IntervalBlockServiceImpl;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class IntervalBlockServiceImplTests {

    @Test
    public void findAllByMeterReading_returnsIntervalBlocks() {
        IntervalBlockRepository repository = mock(IntervalBlockRepository.class);
        IntervalBlockService service = new IntervalBlockServiceImpl();
        service.setRepository(repository);

        service.findAllByMeterReadingId(1L);

        verify(repository).findAllByMeterReadingId(1L);
    }
}
