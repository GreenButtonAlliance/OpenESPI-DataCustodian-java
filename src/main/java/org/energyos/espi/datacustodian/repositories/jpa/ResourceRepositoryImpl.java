package org.energyos.espi.datacustodian.repositories.jpa;

import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.domain.Linkable;
import org.energyos.espi.datacustodian.repositories.ResourceRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
class ResourceRepositoryImpl implements ResourceRepository {
    @PersistenceContext
    protected EntityManager em;

    @Override
    public void persist(IdentifiedObject resource) {
        em.persist(resource);
    }

    @Override
    public IdentifiedObject findByRelatedHref(String href, Linkable linkable) {
        return (IdentifiedObject)em.createNamedQuery(linkable.getRelatedLinkQuery()).setParameter("href", href).getSingleResult();
    }

    @Override
    public List<IdentifiedObject> findAllRelated(Linkable linkable) {
        return em.createNamedQuery(linkable.getAllRelatedQuery()).setParameter("relatedLinkHrefs", linkable.getRelatedLinkHrefs()).getResultList();
    }

    @Override
    public <T> T findByUUID(UUID uuid, Class<T> clazz) {
        return (T)em.createQuery("SELECT resource FROM " + clazz.getCanonicalName() + " resource WHERE resource.uuid = :uuid")
                .setParameter("uuid", uuid.toString().toUpperCase())
                .getSingleResult();
    }
}
