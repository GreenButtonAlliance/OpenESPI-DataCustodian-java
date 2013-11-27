package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.web.ExportFilter;

import java.io.IOException;
import java.io.OutputStream;

public interface ExportService {
    void exportSubscription(String subscriptionHashedId, OutputStream stream, ExportFilter exportFilter) throws IOException;

    void exportUsagePoints(Long retailCustomerId, OutputStream stream, ExportFilter exportFilter) throws IOException;
}
