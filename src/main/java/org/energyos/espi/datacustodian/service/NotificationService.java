package org.energyos.espi.datacustodian.service;

import org.energyos.espi.common.domain.Subscription;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void notify(Subscription subscription);
}
