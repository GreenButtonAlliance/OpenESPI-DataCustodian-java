package org.energyos.espi.datacustodian.service.impl;

import org.energyos.espi.datacustodian.domain.BatchList;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.UpdateService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    private UsagePointService usagePointService;

    public BatchList updatedResources(Subscription subscription) {
        List<UsagePoint> usagePoints = usagePointService.findAllUpdatedFor(subscription);

        BatchList batchList = new BatchList();

        for(UsagePoint usagePoint: usagePoints) {
            batchList.getResources().add(usagePoint.getSelfLink());
        }

        return batchList;
    }

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }
}
