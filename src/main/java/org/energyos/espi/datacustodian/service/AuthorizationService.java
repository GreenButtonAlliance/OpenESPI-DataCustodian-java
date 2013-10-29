package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.Authorization;
import org.energyos.espi.datacustodian.domain.Subscription;

public interface AuthorizationService {
    Authorization createAuthorization(Subscription subscription, String accessToken);

    void persist(Authorization authorization);
}
