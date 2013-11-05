package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.datacustodian.domain.BatchList;
import org.energyos.espi.datacustodian.domain.Routes;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UpdateServiceImpl updateService;

    public void notify(Subscription subscription) {
        String notificationURI = subscription.getThirdParty().getNotificationURI();
        BatchList batchList = new BatchList();
        batchList.getResources().add(Routes.newDataCustodianSubscription(subscription.getHashedId()));
        restTemplate.postForLocation(notificationURI, batchList);
    }

    public void setUpdateService(UpdateServiceImpl updateService) {
        this.updateService = updateService;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
