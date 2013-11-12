package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.repositories.ResourceRepository;
import org.energyos.espi.datacustodian.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceRepository repository;

    @Override
    public void persist(IdentifiedObject resource) {
        repository.persist(resource);
    }

    public void setRepository(ResourceRepository repository) {
        this.repository = repository;
    }
}
