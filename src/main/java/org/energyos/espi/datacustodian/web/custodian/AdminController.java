package org.energyos.espi.datacustodian.web.custodian;

import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.NotificationService;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.datacustodian.web.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class AdminController extends BaseController {

    @Autowired
    private ConsumerTokenServices tokenServices;
    
    private TokenStore tokenStore;
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    @Autowired
    private NotificationService notificationService;

//	private EspiUserApprovalHandler userApprovalHandler;

    @RequestMapping(value = Routes.DATA_CUSTODIAN_NOTIFY_THIRD_PARTY, method = RequestMethod.GET)
    public String notifyThirdParty() throws Exception {
        for(Subscription subscription : subscriptionService.findAll()) {
           notificationService.notify(subscription, null, null);
        }
        return "redirect:" + Routes.DATA_CUSTODIAN_HOME;
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_REMOVE_ALL_OAUTH_TOKENS, method = RequestMethod.GET)
    public String revokeToken() throws Exception {

        for(OAuth2AccessToken t: ((TokenStore) tokenServices).findTokensByClientId("third_party")) {
            tokenServices.revokeToken(t.getValue());
        }

        return "redirect:" + Routes.DATA_CUSTODIAN_HOME;
    }
    
    //**************************************************************************************************
    //*
    //*                     Spring Security OAuth AdminController Methods
    //*
    //**************************************************************************************************

//	@RequestMapping("/oauth/cache_approvals")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void startCaching() throws Exception {
//		userApprovalHandler.setUseApprovalStore(true);
//	}
//
//	@RequestMapping("/oauth/uncache_approvals")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void stopCaching() throws Exception {
//		userApprovalHandler.setUseApprovalStore(false);
//	}
//
//	@RequestMapping("/oauth/users/{user}/tokens")
//	@ResponseBody
//	public Collection<OAuth2AccessToken> listTokensForUser(@PathVariable String user, Principal principal)
//			throws Exception {
//		checkResourceOwner(user, principal);
//		return tokenStore.findTokensByUserName(user);
//	}
//
//	@RequestMapping(value = "/oauth/users/{user}/tokens/{token}", method = RequestMethod.DELETE)
//	public ResponseEntity<Void> revokeToken(@PathVariable String user, @PathVariable String token, Principal principal)
//			throws Exception {
//		checkResourceOwner(user, principal);
//		if (tokenServices.revokeToken(token)) {
//			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
//		} else {
//			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);			
//		}
//	}
//
//	@RequestMapping("/oauth/clients/{client}/tokens")
//	@ResponseBody
//	public Collection<OAuth2AccessToken> listTokensForClient(@PathVariable String client) throws Exception {
//		return tokenStore.findTokensByClientId(client);
//	}
//
//	private void checkResourceOwner(String user, Principal principal) {
//		if (principal instanceof OAuth2Authentication) {
//			OAuth2Authentication authentication = (OAuth2Authentication) principal;
//			if (!authentication.isClientOnly() && !user.equals(principal.getName())) {
//				throw new AccessDeniedException(String.format("User '%s' cannot obtain tokens for user '%s'",
//						principal.getName(), user));
//			}
//		}
//	}
//
//	/**
//	 * @param userApprovalHandler the userApprovalHandler to set
//	 */
//	public void setUserApprovalHandler(EspiUserApprovalHandler userApprovalHandler) {
//		this.userApprovalHandler = userApprovalHandler;
//	}
//
//	/**
//	 * @param tokenServices the consumerTokenServices to set
//	 */
//	public void setTokenServices(ConsumerTokenServices tokenServices) {
//		this.tokenServices = tokenServices;
//	}
//	
//	/**
//	 * @param tokenStore the tokenStore to set
//	 */
//	public void setTokenStore(TokenStore tokenStore) {
//		this.tokenStore = tokenStore;
//	}    
}
