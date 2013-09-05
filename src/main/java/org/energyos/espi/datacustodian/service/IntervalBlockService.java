package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.IntervalBlock;
import org.energyos.espi.datacustodian.repositories.IntervalBlockRepository;

import java.util.List;

public interface IntervalBlockService {
    List<IntervalBlock> findAllByMeterReadingId(Long meterReadingId);

    void setRepository(IntervalBlockRepository repository);
}
