package org.energyos.espi.datacustodian.service;

import org.energyos.espi.datacustodian.domain.ThirdParty;

import java.util.List;

public interface ThirdPartyService {

    public List<ThirdParty> findAll();

    ThirdParty findById(Long id);

    void persist(ThirdParty thirdParty);

    ThirdParty findByClientId(String thirdPartyClientId);
}
