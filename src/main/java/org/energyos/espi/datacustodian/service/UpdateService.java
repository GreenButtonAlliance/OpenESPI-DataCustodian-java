package org.energyos.espi.common.service;

import org.energyos.espi.common.domain.BatchList;
import org.energyos.espi.common.domain.Subscription;

public interface UpdateService {
    BatchList updatedResources(Subscription subscription);
}
