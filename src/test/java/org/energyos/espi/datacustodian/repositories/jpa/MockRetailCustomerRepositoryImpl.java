package org.energyos.espi.datacustodian.repositories.jpa;

import org.energyos.espi.datacustodian.models.RetailCustomer;
import org.energyos.espi.datacustodian.repositories.RetailCustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MockRetailCustomerRepositoryImpl implements RetailCustomerRepository {

    @Override
    public List<RetailCustomer> findAll() {
        ArrayList customers = new ArrayList<RetailCustomer>();

        RetailCustomer alanTuring = new RetailCustomer();
        alanTuring.setFirstName("Alan");
        alanTuring.setLastName("Turing");

        customers.add(alanTuring);

        return customers;
    }
}
