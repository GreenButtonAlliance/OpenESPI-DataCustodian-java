package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.BatchList;
import org.energyos.espi.datacustodian.domain.Subscription;

public interface UpdateService {
    BatchList updatedResources(Subscription subscription);
}
