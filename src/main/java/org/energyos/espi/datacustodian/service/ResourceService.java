package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.domain.Linkable;

import java.util.List;

public interface ResourceService {
    void persist(IdentifiedObject resource);

    IdentifiedObject findByRelatedHref(String relatedHref, Linkable linkable);

    List<IdentifiedObject> findAllRelated(Linkable resource);
}
