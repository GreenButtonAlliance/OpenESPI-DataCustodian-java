package org.energyos.espi.datacustodian.service;

import java.io.IOException;
import java.io.OutputStream;

public interface ExportService {
    void exportSubscription(String subscriptionHashedId, OutputStream stream) throws IOException;

    void exportUsagePoints(Long retailCustomerId, OutputStream stream) throws IOException;
}
