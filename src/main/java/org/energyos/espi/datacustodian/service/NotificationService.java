package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.Subscription;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void notify(Subscription subscription);
}
