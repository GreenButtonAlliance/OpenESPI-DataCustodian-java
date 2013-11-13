package org.energyos.espi.datacustodian.repositories;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.domain.Linkable;

import java.util.List;

public interface ResourceRepository {
    void persist(IdentifiedObject resource);

    IdentifiedObject findByRelatedHref(String href, Linkable linkable);

    List<IdentifiedObject> findAllRelated(Linkable linkable);
}
