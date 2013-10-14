package org.energyos.espi.datacustodian.oauth;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.Routes;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class EspiTokenEnhancer implements TokenEnhancer {

    @Autowired
    private SubscriptionService service;

    private String baseURL;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        Map<String, Object> additionalInformation = new HashMap<>();

        Subscription subscription = service.createSubscription((RetailCustomer) authentication.getPrincipal());

        additionalInformation.put("resource", baseURL + Routes.DataCustodianSubscription.replace("{SubscriptionID}", subscription.getUUID().toString()));

        token.setAdditionalInformation(additionalInformation);

        return token;
    }

    public void setService(SubscriptionService service) {
        this.service = service;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
}
