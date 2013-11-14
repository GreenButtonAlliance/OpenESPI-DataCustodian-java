package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.repositories.MeterReadingRepository;

import java.util.UUID;

public interface MeterReadingService {
    MeterReading findById(Long meterReadingId);

    void persist(MeterReading meterReading);

    void setRepository(MeterReadingRepository repository);

    MeterReading findByUUID(UUID uuid);
}
