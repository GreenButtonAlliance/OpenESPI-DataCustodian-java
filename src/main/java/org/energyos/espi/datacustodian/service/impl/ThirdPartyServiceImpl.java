package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.datacustodian.domain.ThirdParty;
import org.energyos.espi.datacustodian.repositories.ThirdPartyRepository;
import org.energyos.espi.datacustodian.service.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ThirdPartyServiceImpl implements ThirdPartyService {

    @Autowired
    private ThirdPartyRepository repository;

    @Override
    public List<ThirdParty> findAll() {
        return repository.findAll();
    }

    public void setRepository(ThirdPartyRepository repository) {
        this.repository = repository;
    }
}
