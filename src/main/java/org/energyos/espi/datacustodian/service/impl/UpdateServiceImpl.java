package org.energyos.espi.datacustodian.service.impl;

import java.util.List;

import org.energyos.espi.common.domain.BatchList;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.datacustodian.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    private UsagePointService usagePointService;

    public BatchList updatedResources(Subscription subscription) {
        List<UsagePoint> usagePoints = usagePointService.findAllUpdatedFor(subscription);

        BatchList batchList = new BatchList();

        for(UsagePoint usagePoint: usagePoints) {
            batchList.getResources().add(usagePoint.getSelfHref());
        }

        return batchList;
    }

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }
}
