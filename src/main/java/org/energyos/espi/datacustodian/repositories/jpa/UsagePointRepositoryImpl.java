/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.energyos.espi.datacustodian.repositories.jpa;

import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.repositories.UsagePointRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UsagePointRepositoryImpl implements UsagePointRepository{

    @PersistenceContext
    protected EntityManager em;

    @SuppressWarnings("unchecked") // Third party code Query.getResultList() returns an unparameterized List
	public List<UsagePoint> findAllByRetailCustomerId(Long id) {
        return (List<UsagePoint>)this.em.createNamedQuery(UsagePoint.QUERY_FIND_ALL_BY_RETAIL_CUSTOMER_ID)
                .setParameter("retailCustomerId", id)
                .getResultList();
    }

    public UsagePoint findById(Long id) {
        return this.em.find(UsagePoint.class, id);
    }

    @Transactional
    public void persist(UsagePoint up){
        this.em.persist(up);
    }
}
