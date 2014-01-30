package org.energyos.espi.datacustodian.oauth;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.energyos.espi.common.domain.Authorization;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.ApplicationInformationService;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public class EspiTokenEnhancer implements TokenEnhancer {

	@Autowired
	private ApplicationInformationService applicationInformationService;
	
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private AuthorizationService authorizationService;

    private String baseURL;  // "baseURL" is a "tokenEnhancer" bean property defined in the oauth-AS-config.xml file 

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

//		  Original Pivotal Code    	
//        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
//        Map<String, Object> additionalInformation = new HashMap<>();     
        
        // Create Subscription and add resourceURI to /oath/token response
//        Subscription subscription = subscriptionService.createSubscription(authentication);   
//        additionalInformation.put("resourceURI", baseURL + Routes.BATCH_SUBSCRIPTION.replace("{subscriptionId}", subscription.getId().toString()));        

        // Create Authorization and add authorizationURI to /oath/token response        
//        Authorization authorization = authorizationService.createAuthorization(subscription, token.getValue());           
//        additionalInformation.put("authorizationURI", baseURL + Routes.DATA_CUSTODIAN_AUTHORIZATION.replace("{AuthorizationID}", authorization.getId().toString()));        
//
//        token.setAdditionalInformation(additionalInformation);
    	
    	//Spring Security Oauth 2.0.0.M2 code
		DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
		result.setAdditionalInformation(Collections.singletonMap("client_id", (Object) authentication.getOAuth2Request().getClientId()));
		
        // Create Subscription and add resourceURI to /oath/token response
        Subscription subscription = subscriptionService.createSubscription(authentication);   
        result.getAdditionalInformation().put("resourceURI", baseURL + Routes.BATCH_SUBSCRIPTION.replace("{subscriptionId}", subscription.getId().toString()));        

        // Create Authorization and add authorizationURI to /oath/token response        
        Authorization authorization = authorizationService.createAuthorization(subscription, result.getValue());           
        result.getAdditionalInformation().put("authorizationURI", baseURL + Routes.DATA_CUSTODIAN_AUTHORIZATION.replace("{AuthorizationID}", authorization.getId().toString()));        	
        
        subscription.setAuthorization(authorization);
		subscription.setUpdated(new GregorianCalendar());        
        subscriptionService.merge(subscription);

        authorization.setApplicationInformation(applicationInformationService.findByClientId(authentication.getOAuth2Request().getClientId()));
        authorization.setThirdParty(authentication.getOAuth2Request().getClientId());
        authorization.setRetailCustomer((RetailCustomer)authentication.getPrincipal());
        authorization.setAccessToken(accessToken.getValue());        
        authorization.setTokenType(accessToken.getTokenType());
        authorization.setExpiresIn((long) accessToken.getExpiresIn());
        
		if(accessToken.getRefreshToken() != null) {
			authorization.setRefreshToken(accessToken.getRefreshToken());			
		}
		
		// Remove "[" and "]" surrounding Scope in accessToken structure
        authorization.setScope(accessToken.getScope().toString().substring(1, (accessToken.getScope().toString().length()-1)));
        authorization.setAuthorizationURI(baseURL + Routes.DATA_CUSTODIAN_AUTHORIZATION.replace("{AuthorizationID}", authorization.getId().toString()));
        authorization.setResourceURI(baseURL + Routes.BATCH_SUBSCRIPTION.replace("{subscriptionId}", subscription.getId().toString()));
		authorization.setUpdated(new GregorianCalendar());
		authorization.setStatus("1"); 	// Set authorization record status as "Active"
        authorizationService.merge(authorization);

//		  Original Pivotal code
//        return token;
        
        //Spring Security Oauth 2.0.0.M2 code
        return result;
    }

    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}
