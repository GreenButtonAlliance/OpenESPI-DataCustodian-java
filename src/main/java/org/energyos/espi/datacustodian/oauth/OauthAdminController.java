/*
 * Copyright 2006-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.energyos.espi.datacustodian.oauth;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.energyos.espi.datacustodian.oauth.EspiUserApprovalHandler;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
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

	@RequestMapping("custodian/oauth/cache_approvals")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void startCaching() throws Exception {
		userApprovalHandler.setUseApprovalStore(true);
	}

	@RequestMapping("custodian/oauth/uncache_approvals")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void stopCaching() throws Exception {
		userApprovalHandler.setUseApprovalStore(false);
	}
	
	@RequestMapping(value="custodian/oauth/tokens/clients/{clientId}/retailcustomers/{userId}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public Collection<OAuth2AccessToken> listTokensForUser(@PathVariable String clientId, @PathVariable String userId,
			Principal principal) throws Exception {
		checkResourceOwner(userId, principal);

		return enhance(tokenStore.findTokensByClientIdAndUserName(clientId, userId));
	}
	
	@RequestMapping(value = "custodian/oauth/tokens/{token}/retailcustomers/{userId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> revokeToken(@PathVariable String userId, @PathVariable String token, Principal principal)
			throws Exception {
		checkResourceOwner(userId, principal);
		if (tokenServices.revokeToken(token)) {
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);			
		}
	}
	
	@RequestMapping(value="custodian/oauth/tokens/clients/{clientId}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public Collection<OAuth2AccessToken> listTokensForClient(@PathVariable String clientId) throws Exception {
		return tokenStore.findTokensByClientId(clientId);
	}

	private Collection<OAuth2AccessToken> enhance(Collection<OAuth2AccessToken> tokens) {
		Collection<OAuth2AccessToken> result = new ArrayList<OAuth2AccessToken>();
		for (OAuth2AccessToken prototype : tokens) {
			DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(prototype);
			OAuth2Authentication authentication = tokenStore.readAuthentication(token);
			if (authentication == null) {
				continue;
			}
			String clientId = authentication.getOAuth2Request().getClientId();
			if (clientId != null) {
				Map<String, Object> map = new HashMap<String, Object>(token.getAdditionalInformation());
				map.put("client_id", clientId);
				token.setAdditionalInformation(map);
				result.add(token);
			}
		}
		return result;
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
	
}
