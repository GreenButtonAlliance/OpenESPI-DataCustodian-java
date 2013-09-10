package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.datacustodian.domain.IntervalBlock;
import org.energyos.espi.datacustodian.repositories.IntervalBlockRepository;
import org.energyos.espi.datacustodian.service.IntervalBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service
public class IntervalBlockServiceImpl implements IntervalBlockService {

    @Autowired
    protected IntervalBlockRepository repository;

    @Override
    public List<IntervalBlock> findAllByMeterReadingId(Long meterReadingId) {
        return repository.findAllByMeterReadingId(meterReadingId);
    }

    public void setRepository(IntervalBlockRepository repository) {
        this.repository = repository;
    }
}
