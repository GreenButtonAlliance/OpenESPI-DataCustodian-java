package org.energyos.espi.datacustodian.repositories;

import org.energyos.espi.datacustodian.models.RetailCustomer;

import java.util.List;

public interface RetailCustomerRepository {

    List<RetailCustomer> findAll();
}
