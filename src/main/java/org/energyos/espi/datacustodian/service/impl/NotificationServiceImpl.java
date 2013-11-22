package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.common.domain.BatchList;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.datacustodian.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private RestTemplate restTemplate;

    public void notify(Subscription subscription) {
        String notificationURI = subscription.getApplicationInformation().getThirdPartyDefaultNotifyResource();
        BatchList batchList = new BatchList();
        batchList.getResources().add(Routes.buildDataCustodianSubscription(subscription.getHashedId()));
        restTemplate.postForLocation(notificationURI, batchList);
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
