package org.energyos.espi.datacustodian.oauth;

import org.energyos.espi.common.domain.ApplicationInformation;
import org.energyos.espi.common.domain.Authorization;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.ApplicationInformationService;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.common.utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.transaction.annotation.Transactional;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EspiTokenEnhancer implements TokenEnhancer {

	@Autowired
	private ApplicationInformationService applicationInformationService;
	
    @Autowired
    private SubscriptionService subscriptionService;
    
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private AuthorizationService authorizationService;

    private String baseURL;  // "baseURL" is a "tokenEnhancer" bean property defined in the oauth-config.xml file 

    @Transactional
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        Map<String, Object> additionalInformation = new HashMap<>();  
        RetailCustomer retailCustomer = (RetailCustomer) authentication.getPrincipal();
        
        // Create Subscription and add resourceURI to /oath/token response
        Subscription subscription = subscriptionService.createSubscription(authentication);   
        additionalInformation.put("resourceURI", baseURL + Routes.BATCH_SUBSCRIPTION.replace("{subscriptionId}", subscription.getId().toString()));        

        // Create Authorization and add authorizationURI to /oath/token response        
        Authorization authorization = authorizationService.createAuthorization(subscription, token.getValue());           
        additionalInformation.put("authorizationURI", baseURL + Routes.DATA_CUSTODIAN_AUTHORIZATION.replace("{AuthorizationID}", authorization.getId().toString()));        

        token.setAdditionalInformation(additionalInformation);
        
        subscription.setAuthorization(authorization);
		subscription.setUpdated(new GregorianCalendar());        
        subscriptionService.merge(subscription);

        // link in the usage points associated with this subscription
        List<Long> usagePointIds = resourceService.findAllIdsByXPath(retailCustomer.getId(), UsagePoint.class);
        Iterator<Long> it = usagePointIds.iterator();
        while (it.hasNext()) {
        	UsagePoint up = resourceService.findById(it.next(), UsagePoint.class);
        	up.setSubscription(subscription);
        	resourceService.persist(up);  // maybe not needed??
        }
        
        authorization.setApplicationInformation(applicationInformationService.findByClientId(authentication.getOAuth2Request().getClientId()));
        authorization.setThirdParty(authentication.getOAuth2Request().getClientId());
        authorization.setRetailCustomer(retailCustomer);
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

        return token;
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

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
}
