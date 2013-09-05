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

import org.energyos.espi.datacustodian.domain.IntervalBlock;
import org.energyos.espi.datacustodian.repositories.IntervalBlockRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class IntervalBlockRepositoryImpl implements IntervalBlockRepository {

    @PersistenceContext
    protected EntityManager em;

    @Override
    public List<IntervalBlock> findAllByMeterReadingId(Long meterReadingId) {
        return (List<IntervalBlock>)this.em.createNamedQuery(IntervalBlock.QUERY_ALL_BY_METER_READING_ID)
                .setParameter("meterReadingId", meterReadingId).getResultList();
    }
}
