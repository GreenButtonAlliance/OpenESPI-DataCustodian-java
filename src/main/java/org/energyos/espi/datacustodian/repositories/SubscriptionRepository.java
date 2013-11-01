package org.energyos.espi.datacustodian.repositories;

import org.energyos.espi.datacustodian.domain.Subscription;

import java.util.List;

public interface SubscriptionRepository {
    void persist(Subscription subscription);

    List<Subscription> findAll();

    Subscription findByHashedId(String hashedId);
}
