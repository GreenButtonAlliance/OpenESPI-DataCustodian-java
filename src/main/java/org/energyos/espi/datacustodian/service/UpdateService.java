package org.energyos.espi.datacustodian.service;

import org.energyos.espi.common.domain.BatchList;
import org.energyos.espi.common.domain.Subscription;

public interface UpdateService {
    BatchList updatedResources(Subscription subscription);
}
