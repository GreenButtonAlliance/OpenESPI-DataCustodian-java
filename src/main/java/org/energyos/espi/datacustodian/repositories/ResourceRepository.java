package org.energyos.espi.datacustodian.repositories;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;

public interface ResourceRepository {
    void persist(IdentifiedObject resource);
}
