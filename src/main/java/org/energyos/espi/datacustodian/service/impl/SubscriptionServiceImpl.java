package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.repositories.SubscriptionRepository;
import org.energyos.espi.datacustodian.service.SubscriptionService;
import org.energyos.espi.datacustodian.service.ThirdPartyService;
import org.energyos.espi.datacustodian.utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Autowired
    private SubscriptionRepository repository;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Override
    public Subscription createSubscription(OAuth2Authentication authentication) {
        Subscription subscription = new Subscription();
        subscription.setUUID(UUID.randomUUID());
        subscription.setThirdParty(thirdPartyService.findByClientId(authentication.getOAuth2Request().getClientId()));
        subscription.setRetailCustomer((RetailCustomer)authentication.getPrincipal());
        subscription.setLastUpdate(DateConverter.epoch());
        repository.persist(subscription);

        return subscription;
    }

    @Override
    public List<Subscription> findAll() {
        return repository.findAll();
    }

    @Override
    public void persist(Subscription subscription) {
        repository.persist(subscription);
    }

    public void setRepository(SubscriptionRepository repository) {
        this.repository = repository;
    }

    public void setThirdPartyService(ThirdPartyService thirdPartyService) {
        this.thirdPartyService = thirdPartyService;
    }
}
