package org.energyos.espi.datacustodian.repositories;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.domain.Linkable;

import java.util.List;
import java.util.UUID;

public interface ResourceRepository {
    void persist(IdentifiedObject resource);

    List<IdentifiedObject> findAllParentsByRelatedHref(String href, Linkable linkable);

    List<IdentifiedObject> findAllRelated(Linkable linkable);

    <T> T findByUUID(UUID uuid, Class<T> clazz);
}
