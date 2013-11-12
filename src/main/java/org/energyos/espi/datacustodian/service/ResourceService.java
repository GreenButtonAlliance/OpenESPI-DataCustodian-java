package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.domain.Linkable;

public interface ResourceService {
    void persist(IdentifiedObject resource);

    IdentifiedObject findByRelatedHref(String relatedHref, Linkable linkable);
}
