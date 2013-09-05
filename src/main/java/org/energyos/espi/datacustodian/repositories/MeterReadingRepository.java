package org.energyos.espi.datacustodian.repositories;

import org.energyos.espi.datacustodian.domain.MeterReading;

/**
 * Created with IntelliJ IDEA.
 * User: pivotal
 * Date: 9/5/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MeterReadingRepository {
    MeterReading findAllById(Long meterReadingId);

    void persist(MeterReading meterReading);
}
