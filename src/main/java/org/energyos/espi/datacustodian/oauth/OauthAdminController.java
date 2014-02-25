/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.energyos.espi.datacustodian.oauth;

import java.security.Principal;
import java.util.Collection;

import org.energyos.espi.datacustodian.oauth.EspiUserApprovalHandler;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for resetting the token store for testing purposes.
 * 
 * @author Dave Syer
 */
@Controller
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class OauthAdminController extends BaseController {

	private ConsumerTokenServices tokenServices;

	private TokenStore tokenStore;

	private EspiUserApprovalHandler userApprovalHandler;

	/**
	 * 
	 * Display OAuth Token Management screen
	 * 
	 * @return URL of OAuth Token Management screen
	 * 
	 */
	@RequestMapping(value="custodian/oauth/tokens", method=RequestMethod.GET)
	@ResponseBody
	public ModelAndView manageTokens() {
		return new ModelAndView("/custodian/oauth/tokens");
	}

	/**
	 * 
	 * Activate Approval Store processing
	 * 
	 * @throws Exception
	 * 
	 */
	
	@RequestMapping("custodian/oauth/cache_approvals")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void startCaching() throws Exception {
		userApprovalHandler.setUseApprovalStore(true);
	}

	/**
	 * 
	 * Deactivate Approval Store processing
	 * 
	 * @throws Exception
	 * 
	 */
	
	@RequestMapping("custodian/oauth/uncache_approvals")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void stopCaching() throws Exception {
		userApprovalHandler.setUseApprovalStore(false);
	}

	/**
	 * 
	 * List OAuth Tokens for a specific Retail Customer UserID
	 * 
	 * @param userID  
	 * @param principal
	 * @return Collection of access tokens
	 * @throws Exception
	 * 
	 */
	
	@RequestMapping(value="custodian/oauth/tokens/retailcustomers/{userID}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public Collection<OAuth2AccessToken> listTokensForUser(@PathVariable String userID, Principal principal)
			throws Exception {
		checkResourceOwner(userID, principal);
		return tokenStore.findTokensByUserName(userID);
	}

	/**
	 * 
	 * Delete a specific OAuth Token for a specific Retail Customer UserID
	 * 
	 * @param userID
	 * @param token
	 * @param principal
	 * @return
	 * @throws Exception
	 * 
	 */
	
	@RequestMapping(value = "custodian/oauth/tokens/{token}/retailcustomers/{userID}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> revokeToken(@PathVariable String userID, @PathVariable String token, Principal principal)
			throws Exception {
		checkResourceOwner(userID, principal);
		if (tokenServices.revokeToken(token)) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);			
		}
	}

	/**
	 * 
	 * List OAuth Tokens for a specific Client
	 * 
	 * @param clientID
	 * @return Collection of access tokens
	 * @throws Exception
	 * 
	 */
	
	@RequestMapping(value="custodian/oauth/tokens/clients/{clientID}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public Collection<OAuth2AccessToken> listTokensForClient(@PathVariable String clientID) throws Exception {
		return tokenStore.findTokensByClientId(clientID);
	}

	private void checkResourceOwner(String user, Principal principal) {
		if (principal instanceof OAuth2Authentication) {
			OAuth2Authentication authentication = (OAuth2Authentication) principal;
			if (!authentication.isClientOnly() && !user.equals(principal.getName())) {
				throw new AccessDeniedException(String.format("User '%s' cannot obtain tokens for user '%s'",
						principal.getName(), user));
			}
		}
	}

	/**
	 * @param userApprovalHandler the userApprovalHandler to set
	 */
	public void setUserApprovalHandler(EspiUserApprovalHandler userApprovalHandler) {
		this.userApprovalHandler = userApprovalHandler;
	}

	/**
	 * @param tokenServices the consumerTokenServices to set
	 */
	public void setTokenServices(ConsumerTokenServices tokenServices) {
		this.tokenServices = tokenServices;
	}
	
	/**
	 * @param tokenStore the tokenStore to set
	 */
	public void setTokenStore(TokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}

}
