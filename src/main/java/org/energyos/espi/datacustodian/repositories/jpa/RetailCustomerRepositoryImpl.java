package org.energyos.espi.datacustodian.repositories.jpa;

import org.energyos.espi.datacustodian.models.RetailCustomer;
import org.energyos.espi.datacustodian.repositories.RetailCustomerRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RetailCustomerRepositoryImpl implements RetailCustomerRepository {

    @PersistenceContext
    protected EntityManager em;

    @Override
    public List<RetailCustomer> findAll() {
        return this.em.createQuery("SELECT customer FROM RetailCustomer customer").getResultList();
    }
}
