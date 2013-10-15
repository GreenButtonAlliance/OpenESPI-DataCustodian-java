package org.energyos.espi.datacustodian.repositories;

import org.energyos.espi.datacustodian.domain.Authorization;
import org.energyos.espi.datacustodian.domain.IntervalBlock;

import java.util.List;

public interface AuthorizationRepository {
    void persist(Authorization authorization);
}
