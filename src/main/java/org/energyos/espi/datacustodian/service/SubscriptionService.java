package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.Subscription;

public interface SubscriptionService {
    Subscription createSubscription(RetailCustomer retailCustomer);
}
