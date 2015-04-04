/*
 * Copyright 2013, 2014, 2015 EnergyOS.org
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

package org.energyos.espi.datacustodian.web.customer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.energyos.espi.common.domain.MeterReading;
import org.energyos.espi.common.service.MeterReadingService;
import org.junit.Test;
import org.springframework.ui.ModelMap;

public class MeterReadingControllerTest {

	@Test
	public void show_displaysShowView() {
		MeterReadingController controller = new MeterReadingController();
		controller.setMeterReadingService(mock(MeterReadingService.class));

		assertEquals("/customer/meterreadings/show",
				controller.show(1L, 1L, 1L, new ModelMap()));
	}

	@Test
	public void show_setsMeterReadingModel() {
		MeterReading meterReading = new MeterReading();
		MeterReadingService meterReadingService = mock(MeterReadingService.class);
		MeterReadingController controller = new MeterReadingController();
		controller.setMeterReadingService(meterReadingService);
		when(meterReadingService.findById(1L, 1L, anyLong())).thenReturn(
				meterReading);
		ModelMap model = new ModelMap();

		controller.show(1L, 1L, 1L, model);

		assertEquals(meterReading, model.get("meterReading"));
	}
}
