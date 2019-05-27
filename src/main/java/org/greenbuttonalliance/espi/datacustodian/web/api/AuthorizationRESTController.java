/*
 *     Copyright (c) 2018-2019 Green Button Alliance, Inc.
 *
 *     Portions copyright (c) 2013-2018 EnergyOS.org
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.greenbuttonalliance.espi.datacustodian.web.api;

import com.sun.syndication.io.FeedException;
import org.greenbuttonalliance.espi.common.domain.Authorization;
import org.greenbuttonalliance.espi.common.domain.Routes;
import org.greenbuttonalliance.espi.common.service.AuthorizationService;
import org.greenbuttonalliance.espi.common.service.ExportService;
import org.greenbuttonalliance.espi.common.service.ResourceService;
import org.greenbuttonalliance.espi.common.service.RetailCustomerService;
import org.greenbuttonalliance.espi.common.utils.ExportFilter;
import org.greenbuttonalliance.espi.datacustodian.utils.VerifyURLParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
	@GetMapping(value = Routes.ROOT_AUTHORIZATION_COLLECTION, produces = "application/atom+xml")
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

	@GetMapping(value = Routes.ROOT_AUTHORIZATION_MEMBER, produces = "application/atom+xml")
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

	@PostMapping(value = Routes.ROOT_AUTHORIZATION_COLLECTION, consumes = "application/atom+xml", produces = "application/atom+xml")
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

	@PutMapping(value = Routes.ROOT_AUTHORIZATION_MEMBER, consumes = "application/atom+xml", produces = "application/atom+xml")
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

	@DeleteMapping(value = Routes.ROOT_AUTHORIZATION_MEMBER)
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
	@GetMapping(value = Routes.AUTHORIZATION_COLLECTION, produces = "application/atom+xml")
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

	@GetMapping(value = Routes.AUTHORIZATION_MEMBER, produces = "application/atom+xml")
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

	@PostMapping(value = Routes.AUTHORIZATION_COLLECTION, consumes = "application/atom+xml", produces = "application/atom+xml")
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

	@PutMapping(value = Routes.AUTHORIZATION_MEMBER, consumes = "application/atom+xml", produces = "application/atom+xml")
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

	@DeleteMapping(value = Routes.AUTHORIZATION_MEMBER)
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
