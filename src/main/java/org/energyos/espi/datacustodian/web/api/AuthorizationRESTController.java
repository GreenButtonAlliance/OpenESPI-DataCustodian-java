/*Copyright 2013, 2014, 2015 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.energyos.espi.datacustodian.web.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.energyos.espi.common.domain.Authorization;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.utils.ExportFilter;
import org.energyos.espi.datacustodian.utils.VerifyURLParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sun.syndication.io.FeedException;

@Controller
public class AuthorizationRESTController {

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private RetailCustomerService retailCustomerService;

	@Autowired
	// @Qualifier("tokenServices")
	private DefaultTokenServices tokenService;

	@Autowired
	private ExportService exportService;

	@Autowired
	private ResourceService resourceService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
	}

	// ROOT RESTful Forms
	//
	@RequestMapping(value = Routes.ROOT_AUTHORIZATION_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.ROOT_AUTHORIZATION_COLLECTION, params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		String accessToken = request.getHeader("authorization").replace(
				"Bearer ", "");
		Authorization authorization = authorizationService
				.findByAccessToken(accessToken);

		// we know this is a client-access-token or a datacustodian-access-token
		// if it is a datacustodian-access-token, it can get everything

		if (authorization.getApplicationInformation().getClientId()
				.equals("data_custodian_admin")) {
			exportService.exportAuthorizations(response.getOutputStream(),
					new ExportFilter(params));
		} else {
			// anything else that gets here is a third party
			// (client-access-token) and needs to be
			// restricted in access scope
			exportService.exportAuthorizations(authorization,
					response.getOutputStream(), new ExportFilter(params));
		}
	}

	@RequestMapping(value = Routes.ROOT_AUTHORIZATION_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long authorizationId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.ROOT_AUTHORIZATION_MEMBER, params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			exportService.exportAuthorization(authorizationId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.ROOT_AUTHORIZATION_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			Authorization authorization = this.authorizationService
					.importResource(stream);
			exportService.exportAuthorization(authorization.getId(),
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.ROOT_AUTHORIZATION_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long authorizationId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {
		Authorization authorization = authorizationService
				.findById(authorizationId);

		if (authorization != null) {
			try {

				Authorization newAuthorization = authorizationService
						.importResource(stream);
				authorization.merge(newAuthorization);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	@RequestMapping(value = Routes.ROOT_AUTHORIZATION_MEMBER, method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response,
			@PathVariable Long authorizationId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {
		try {
			Authorization authorization = resourceService.findById(
					authorizationId, Authorization.class);
			String accessToken = authorization.getAccessToken();

			authorizationService.delete(authorization);
			tokenService.revokeToken(accessToken);
			authorizationService.delete(authorization);

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	// XPath RESTful forms
	//
	@RequestMapping(value = Routes.AUTHORIZATION_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.AUTHORIZATION_COLLECTION, params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		exportService.exportAuthorizations(retailCustomerId,
				response.getOutputStream(), new ExportFilter(params));
	}

	@RequestMapping(value = Routes.AUTHORIZATION_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long authorizationId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.AUTHORIZATION_MEMBER, params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			exportService.exportAuthorization(retailCustomerId,
					authorizationId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.AUTHORIZATION_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			Authorization authorization = this.authorizationService
					.importResource(stream);
			retailCustomerService.associateByUUID(retailCustomerId,
					authorization.getUUID());
			exportService.exportAuthorization(retailCustomerId,
					authorization.getId(), response.getOutputStream(),
					new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	//

	@RequestMapping(value = Routes.AUTHORIZATION_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long authorizationId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {
		Authorization authorization = authorizationService.findById(
				retailCustomerId, authorizationId);

		if (authorization != null) {
			try {

				Authorization newAuthorization = authorizationService
						.importResource(stream);
				authorization.merge(newAuthorization);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	@RequestMapping(value = Routes.AUTHORIZATION_MEMBER, method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long authorizationId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {

		try {
			Authorization authorization = authorizationService.findById(
					retailCustomerId, authorizationId);
			String accessToken = authorization.getAccessToken();
			authorizationService.delete(authorization);
			tokenService.revokeToken(accessToken);

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	public void setTokenService(DefaultTokenServices tokenService) {
		this.tokenService = tokenService;
	}

	public void setAuthorizationService(
			AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	public AuthorizationService getAuthorizationService() {
		return this.authorizationService;
	}

	public void setRetailCustomerService(
			RetailCustomerService retailCustomerService) {
		this.retailCustomerService = retailCustomerService;
	}

	public RetailCustomerService getRetailCustomerService() {
		return this.retailCustomerService;
	}

	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	public ExportService getExportService() {
		return this.exportService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public ResourceService getResourceService() {
		return this.resourceService;
	}

}
