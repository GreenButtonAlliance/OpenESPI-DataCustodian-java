package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.datacustodian.domain.Authorization;
import org.energyos.espi.datacustodian.domain.Routes;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.repositories.AuthorizationRepository;
import org.energyos.espi.datacustodian.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    @Autowired
    private AuthorizationRepository repository;

    public void setRepository(AuthorizationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Authorization createAuthorization(Subscription subscription, String accessToken) {
        Authorization authorization = new Authorization();
        authorization.setUUID(UUID.randomUUID());
        authorization.setAccessToken(accessToken);
        authorization.setResource(Routes.DataCustodianSubscription.replace("{SubscriptionID}", subscription.getUUID().toString()));
        repository.persist(authorization);

        return authorization;
    }
}
