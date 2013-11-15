package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.domain.Linkable;
import org.energyos.espi.datacustodian.repositories.ResourceRepository;
import org.energyos.espi.datacustodian.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceRepository repository;

    @Override
    public void persist(IdentifiedObject resource) {
        repository.persist(resource);
    }

    @Override
    public IdentifiedObject findByRelatedHref(String relatedHref, Linkable linkable) {
        return repository.findByRelatedHref(relatedHref, linkable);
    }

    @Override
    public List<IdentifiedObject> findAllRelated(Linkable linkable) {
        return repository.findAllRelated(linkable);
    }

    @Override
    public <T> T findByUUID(UUID uuid, Class<T> clazz) {
        return repository.findByUUID(uuid, clazz);
    }

    public void setRepository(ResourceRepository repository) {
        this.repository = repository;
    }
}
