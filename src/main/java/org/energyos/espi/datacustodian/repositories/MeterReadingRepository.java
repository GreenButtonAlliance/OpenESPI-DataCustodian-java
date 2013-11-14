package org.energyos.espi.datacustodian.repositories;

import org.energyos.espi.datacustodian.domain.MeterReading;

import java.util.UUID;

public interface MeterReadingRepository {
    MeterReading findById(Long meterReadingId);

    void persist(MeterReading meterReading);

    MeterReading findByUUID(UUID uuid);
}
