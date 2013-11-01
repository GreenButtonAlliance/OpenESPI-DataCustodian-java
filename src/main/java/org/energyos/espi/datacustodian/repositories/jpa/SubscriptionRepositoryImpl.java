package org.energyos.espi.datacustodian.repositories.jpa;

import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.repositories.SubscriptionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    @PersistenceContext
    protected EntityManager em;

    @Transactional
    public void persist(Subscription subscription) {
        em.persist(subscription);
    }

    @Override
    public List<Subscription> findAll() {
        return (List<Subscription>)this.em.createNamedQuery(Subscription.QUERY_FIND_ALL).getResultList();
    }

    @Override
    public Subscription findByHashedId(String hashedId) {
        Query query = em.createQuery("SELECT sub FROM Subscription sub WHERE sub.id = :hashedId");
        query.setParameter("hashedId", Long.valueOf(hashedId));
        return (Subscription)query.getSingleResult();
    }
}
