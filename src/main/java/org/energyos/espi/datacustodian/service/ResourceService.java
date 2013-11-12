package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;

public interface ResourceService {
    void persist(IdentifiedObject resource);
}
