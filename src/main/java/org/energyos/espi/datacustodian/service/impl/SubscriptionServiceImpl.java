package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.repositories.SubscriptionRepository;
import org.energyos.espi.datacustodian.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Autowired
    private SubscriptionRepository repository;

    @Override
    public Subscription createSubscription(RetailCustomer retailCustomer) {
        Subscription subscription = new Subscription();
        subscription.setUUID(UUID.randomUUID());
        subscription.setRetailCustomer(retailCustomer);
        repository.persist(subscription);

        return subscription;
    }

    @Override
    public List<Subscription> findAll() {
        return repository.findAll();
    }

    public void setRepository(SubscriptionRepository repository) {
        this.repository = repository;
    }
}
