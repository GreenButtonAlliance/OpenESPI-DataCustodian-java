package org.energyos.espi.datacustodian.mocks;

import java.util.Collection;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Service;

@Service
public class MockConsumerTokenServices implements ConsumerTokenServices {
//    @Override
    public Collection<OAuth2AccessToken> findTokensByUserName(String s) {
        return null;
    }

//    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String s) {
        return null;
    }

    @Override
    public boolean revokeToken(String s) {
        return false;
    }

//    @Override
    public String getClientId(String s) {
        return null;
    }
}
