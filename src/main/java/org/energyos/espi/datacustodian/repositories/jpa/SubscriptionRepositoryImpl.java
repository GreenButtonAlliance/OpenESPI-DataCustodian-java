package org.energyos.espi.datacustodian.repositories.jpa;

import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.repositories.SubscriptionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    @PersistenceContext
    protected EntityManager em;

    @Transactional
    public void persist(Subscription subscription) {
        em.persist(subscription);
    }
}
