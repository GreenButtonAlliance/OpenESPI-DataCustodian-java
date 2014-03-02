package org.energyos.espi.datacustodian.oauth;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.energyos.espi.common.domain.Authorization;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.ApplicationInformationService;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.SubscriptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.transaction.annotation.Transactional;

public class EspiTokenEnhancer implements TokenEnhancer {

	@Autowired
	private ApplicationInformationService applicationInformationService;
	
    @Autowired
    private SubscriptionService subscriptionService;
    
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private AuthorizationService authorizationService;

    private String baseURL;  // "baseURL" is a "tokenEnhancer" bean property defined in the oauth-AS-config.xml file 

    @Transactional (rollbackFor= {javax.xml.bind.JAXBException.class}, 
                noRollbackFor = {javax.persistence.NoResultException.class, org.springframework.dao.EmptyResultDataAccessException.class })

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
    	
		DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
		result.setAdditionalInformation(Collections.singletonMap("client_id", (Object) authentication.getOAuth2Request().getClientId()));	

		//TODO: Change subscription and authorization table entry creation to work for "client_credentials" access token request
		// Is this access token for an application (i.e. "client_credentials" request?
		if (authentication.isClientOnly() == false){  // No, continue
			
			// Create Subscription and add resourceURI to /oath/token response
			Subscription subscription = subscriptionService.createSubscription(authentication);   
			result.getAdditionalInformation().put("resourceURI", baseURL + Routes.BATCH_SUBSCRIPTION.replace("{subscriptionId}", subscription.getId().toString()));        

			// Create Authorization and add authorizationURI to /oath/token response        
			Authorization authorization = authorizationService.createAuthorization(subscription, result.getValue());           
			result.getAdditionalInformation().put("authorizationURI", baseURL + Routes.DATA_CUSTODIAN_AUTHORIZATION.replace("{AuthorizationID}", authorization.getId().toString()));        	
        
			subscription.setAuthorization(authorization);
			subscription.setUpdated(new GregorianCalendar());        
			subscriptionService.merge(subscription);

			RetailCustomer retailCustomer = (RetailCustomer) authentication.getPrincipal();	
		
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
				authorization.setRefreshToken(accessToken.getRefreshToken().toString());			
			}
		
			// Remove "[" and "]" surrounding Scope in accessToken structure
			authorization.setScope(accessToken.getScope().toString().substring(1, (accessToken.getScope().toString().length()-1)));
			authorization.setAuthorizationURI(baseURL + Routes.DATA_CUSTODIAN_AUTHORIZATION.replace("{AuthorizationID}", authorization.getId().toString()));
			authorization.setResourceURI(baseURL + Routes.BATCH_SUBSCRIPTION.replace("{subscriptionId}", subscription.getId().toString()));
			authorization.setUpdated(new GregorianCalendar());
			authorization.setStatus("1"); 	// Set authorization record status as "Active"
			authorization.setSubscription(subscription);
			authorizationService.merge(authorization);
		}			

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

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
}
