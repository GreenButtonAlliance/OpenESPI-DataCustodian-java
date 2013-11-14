package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.repositories.MeterReadingRepository;
import org.energyos.espi.datacustodian.service.MeterReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MeterReadingServiceImpl implements MeterReadingService {

    @Autowired
    protected MeterReadingRepository repository;

    public void setRepository(MeterReadingRepository repository) {
        this.repository = repository;
    }

    @Override
    public MeterReading findByUUID(UUID uuid) {
        return repository.findByUUID(uuid);
    }

    public MeterReading findById(Long meterReadingId) {
        return repository.findById(meterReadingId);
    }

    @Override
    public void persist(MeterReading meterReading) {
        repository.persist(meterReading);
    }
}
