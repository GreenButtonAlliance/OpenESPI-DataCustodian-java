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

        assertEquals("/customer/meterreadings/show", controller.show(1L, 1L, 1L, new ModelMap()));
    }

    @Test
    public void show_setsMeterReadingModel() {
        MeterReading meterReading = new MeterReading();
        MeterReadingService meterReadingService = mock(MeterReadingService.class);
        MeterReadingController controller = new MeterReadingController();
        controller.setMeterReadingService(meterReadingService);
        when(meterReadingService.findById(1L, 1L, anyLong())).thenReturn(meterReading);
        ModelMap model = new ModelMap();

        controller.show(1L, 1L, 1L, model);

        assertEquals(meterReading, model.get("meterReading"));
    }
}
