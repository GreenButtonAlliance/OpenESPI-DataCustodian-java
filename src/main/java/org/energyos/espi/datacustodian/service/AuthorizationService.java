package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.Authorization;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface AuthorizationService {
    Authorization createAuthorization(Subscription subscription, String accessToken);
}
