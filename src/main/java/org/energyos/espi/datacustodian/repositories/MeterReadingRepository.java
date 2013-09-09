package org.energyos.espi.datacustodian.repositories;

import org.energyos.espi.datacustodian.domain.MeterReading;

public interface MeterReadingRepository {
    MeterReading findById(Long meterReadingId);

    void persist(MeterReading meterReading);
}
