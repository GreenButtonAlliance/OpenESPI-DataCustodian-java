package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.domain.Linkable;

import java.util.List;
import java.util.UUID;

public interface ResourceService {
    void persist(IdentifiedObject resource);

    List<IdentifiedObject> findByAllParentsHref(String relatedHref, Linkable linkable);

    List<IdentifiedObject> findAllRelated(Linkable resource);

    <T> T findByUUID(UUID uuid, Class<T> clazz);
}
