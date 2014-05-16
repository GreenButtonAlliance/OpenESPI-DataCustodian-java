package org.energyos.espi.datacustodian.oauth;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
import org.springframework.security.core.authority.AuthorityUtils;
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

		// Is this a "client_credentials" access token grant_type request?
		if (authentication.isClientOnly() == false){  // No, processing "code" access token grant_type request.
			
			// Create Subscription and add resourceURI to /oath/token response
			Subscription subscription = subscriptionService.createSubscription(authentication);   
			result.getAdditionalInformation().put("resourceURI", baseURL + Routes.BATCH_SUBSCRIPTION.replace("{subscriptionId}", subscription.getId().toString()));        

			// Create Authorization and add authorizationURI to /oath/token response        
			Authorization authorization = authorizationService.createAuthorization(subscription, result.getValue());           
			result.getAdditionalInformation().put("authorizationURI", baseURL + Routes.DATA_CUSTODIAN_AUTHORIZATION.replace("{AuthorizationID}", authorization.getId().toString()));        	
 
			// Update Data Custodian subscription structure
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
			
			// Update Data Custodian authorization structure
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
			
		} else {  // Processing a "client_credentials" access token grant_type request.
	
			// Create Authorization and add authorizationURI to /oath/token response        
			Authorization authorization = authorizationService.createAuthorization(null, result.getValue());           
			result.getAdditionalInformation().put("authorizationURI", baseURL + Routes.DATA_CUSTODIAN_AUTHORIZATION.replace("{AuthorizationID}", authorization.getId().toString()));
		
			// Update Data Custodian authorization structure
			authorization.setApplicationInformation(applicationInformationService.findByClientId(authentication.getOAuth2Request().getClientId()));
			authorization.setThirdParty(authentication.getOAuth2Request().getClientId());
			authorization.setAccessToken(accessToken.getValue());        
			authorization.setTokenType(accessToken.getTokenType());
			authorization.setExpiresIn((long) accessToken.getExpiresIn());
        
			if(accessToken.getRefreshToken() != null) {
				authorization.setRefreshToken(accessToken.getRefreshToken().toString());			
			}
		
			// Remove "[" and "]" surrounding Scope in accessToken structure
			authorization.setScope(accessToken.getScope().toString().substring(1, (accessToken.getScope().toString().length()-1)));
			authorization.setAuthorizationURI(baseURL + Routes.DATA_CUSTODIAN_AUTHORIZATION.replace("{AuthorizationID}", authorization.getId().toString()));
			
			// Determine resourceURI value based on Client's Role
			Set<String> role = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
			
			if (role.contains("ROLE_DC_ADMIN")) {
			authorization.setResourceURI(baseURL + Routes.DATA_CUSTODIAN_RESOURCE_MANAGEMENT);
			
			} else {
				if (role.contains("ROLE_TP_ADMIN")) {
					authorization.setResourceURI(baseURL + Routes.BATCH_BULK_MEMBER.replace("{BulkID}", "**"));
					
				} else {
					if (role.contains("ROLE_UL_ADMIN")) {
						authorization.setResourceURI(baseURL + Routes.BATCH_UPLOAD_MY_DATA.replace("{RetailCustomerID}", "**"));
					}
				}
			} 
			
			authorization.setUpdated(new GregorianCalendar());
			authorization.setStatus("1"); 	// Set authorization record status as "Active"
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
