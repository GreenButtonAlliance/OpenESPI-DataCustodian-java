package org.energyos.espi.datacustodian.oauth;

import org.energyos.espi.common.service.ApplicationInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Component;

@Component
public class ClientDetailsServiceImpl implements ClientDetailsService {
    @Autowired
    private ApplicationInformationService applicationInformationService;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return (ClientDetails)applicationInformationService.loadClientByClientId(clientId);
    }
}
