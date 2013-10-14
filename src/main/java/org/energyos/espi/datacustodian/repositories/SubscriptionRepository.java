package org.energyos.espi.datacustodian.repositories;

import org.energyos.espi.datacustodian.domain.Subscription;

public interface SubscriptionRepository {
    void persist(Subscription subscription);
}
