/*
 *
 *    Copyright (c) 2018-2021 Green Button Alliance, Inc.
 *
 *    Portions (c) 2013-2018 EnergyOS.org
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
 *
 */

package org.greenbuttonalliance.espi.datacustodian.web.api;

import com.sun.syndication.io.FeedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.greenbuttonalliance.espi.common.domain.legacy.Authorization;
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
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * RESTful controller for managing Authorization resources according to the 
 * Green Button Alliance ESPI (Energy Services Provider Interface) specification.
 * 
 * Authorization represents OAuth2 access permissions and tokens for 
 * third-party applications to access customer energy data.
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Authorization", description = "OAuth2 Authorization and Access Token Management API")
public class AuthorizationRESTController {

	private final AuthorizationService authorizationService;
	private final RetailCustomerService retailCustomerService;
	private final DefaultTokenServices tokenService;
	private final ExportService exportService;
	private final ResourceService resourceService;

	@Autowired
	public AuthorizationRESTController(
			AuthorizationService authorizationService,
			RetailCustomerService retailCustomerService,
			DefaultTokenServices tokenService,
			ExportService exportService,
			ResourceService resourceService) {
		this.authorizationService = authorizationService;
		this.retailCustomerService = retailCustomerService;
		this.tokenService = tokenService;
		this.exportService = exportService;
		this.resourceService = resourceService;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
		// Generic exception handler
	}

	// ================================
	// ROOT Authorization Collection APIs
	// ================================

	/**
	 * Retrieves all Authorization resources (root level access).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for filtering and pagination
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM feed generation fails
	 */
	@GetMapping(value = "/Authorization", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get Authorization Collection",
		description = "Retrieves all authorized Authorization resources with optional filtering and pagination. " +
					 "Returns an ATOM feed containing OAuth2 authorization and access token information. " +
					 "Access scope depends on the requesting client's authorization level."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved Authorization collection",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
							 schema = @Schema(description = "ATOM feed containing Authorization entries"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid query parameters provided"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to authorization data"
		)
	})
	public void getAuthorizationCollection(
			HttpServletRequest request, 
			HttpServletResponse response,
			@Parameter(description = "Query parameters for filtering (published-max, published-min, updated-max, updated-min, max-results, start-index)")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/Authorization", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			String accessToken = request.getHeader("authorization").replace(
					"Bearer ", "");
			Authorization authorization = authorizationService.findByAccessToken(accessToken);

			// Determine access scope: data_custodian_admin gets everything, 
			// third-party clients get restricted scope
			if (authorization.getApplicationInformation().getClientId()
					.equals("data_custodian_admin")) {
				exportService.exportAuthorizations(response.getOutputStream(),
						new ExportFilter(params));
			} else {
				// Third-party access with restricted scope
				exportService.exportAuthorizations(authorization,
						response.getOutputStream(), new ExportFilter(params));
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a specific Authorization resource by ID (root level access).
	 * 
	 * @param response HTTP response for streaming ATOM XML content
	 * @param authorizationId Unique identifier for the Authorization
	 * @param params Query parameters for export filtering
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM entry generation fails
	 */
	@GetMapping(value = "/Authorization/{authorizationId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get Authorization by ID",
		description = "Retrieves a specific Authorization resource by its unique identifier. " +
					 "Returns an ATOM entry containing OAuth2 authorization details including " +
					 "access tokens, scope, and application information."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved Authorization",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing Authorization details"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid authorizationId or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this authorization data"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Authorization not found"
		)
	})
	public void getAuthorization(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the Authorization", required = true)
			@PathVariable Long authorizationId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/Authorization/{authorizationId}", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
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

	@PostMapping(value = "/Authorization", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create Authorization",
		description = "Creates a new Authorization resource for OAuth2 access control."
	)
	public void createAuthorization(
			HttpServletResponse response,
			@RequestParam Map<String, String> params, 
			@RequestBody InputStream stream) throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			Authorization authorization = 
					this.authorizationService.importResource(stream);
			exportService.exportAuthorization(authorization.getId(),
					response.getOutputStream(), new ExportFilter(params));
			response.setStatus(HttpServletResponse.SC_CREATED);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@PutMapping(value = "/Authorization/{authorizationId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update Authorization",
		description = "Updates an existing Authorization resource."
	)
	public void updateAuthorization(
			HttpServletResponse response,
			@PathVariable Long authorizationId,
			@RequestParam Map<String, String> params, 
			@RequestBody InputStream stream) throws IOException, FeedException {
		
		try {
			Authorization authorization = 
					authorizationService.findById(authorizationId);

			if (authorization != null) {
				Authorization newAuthorization = 
						authorizationService.importResource(stream);
				authorization.merge(newAuthorization);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@DeleteMapping("/Authorization/{authorizationId}")
	@Operation(
		summary = "Delete Authorization", 
		description = "Removes an Authorization resource and revokes associated OAuth2 tokens."
	)
	public void deleteAuthorization(
			HttpServletResponse response,
			@PathVariable Long authorizationId) {
		
		try {
			Authorization authorization = resourceService.findById(
					authorizationId, Authorization.class);
			
			if (authorization != null) {
				String accessToken = authorization.getAccessToken();
				authorizationService.delete(authorization);
				tokenService.revokeToken(accessToken);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	// =============================================
	// RetailCustomer-scoped Authorization Collection APIs
	// =============================================

	@GetMapping(value = "/RetailCustomer/{retailCustomerId}/Authorization", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get Customer Authorizations",
		description = "Retrieves all Authorization resources for a specific retail customer."
	)
	public void getCustomerAuthorizations(
			HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/RetailCustomer/{retailCustomerId}/Authorization", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			exportService.exportAuthorizations(retailCustomerId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@GetMapping(value = "/RetailCustomer/{retailCustomerId}/Authorization/{authorizationId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get Customer Authorization by ID",
		description = "Retrieves a specific Authorization resource for a retail customer."
	)
	public void getCustomerAuthorization(
			HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long authorizationId,
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/RetailCustomer/{retailCustomerId}/Authorization/{authorizationId}", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
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

	@PostMapping(value = "/RetailCustomer/{retailCustomerId}/Authorization", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create Customer Authorization",
		description = "Creates a new Authorization resource for a specific retail customer."
	)
	public void createCustomerAuthorization(
			HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params, 
			@RequestBody InputStream stream) throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			Authorization authorization = 
					this.authorizationService.importResource(stream);
			retailCustomerService.associateByUUID(retailCustomerId,
					authorization.getUUID());
			exportService.exportAuthorization(retailCustomerId,
					authorization.getId(), response.getOutputStream(),
					new ExportFilter(params));
			response.setStatus(HttpServletResponse.SC_CREATED);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@PutMapping(value = "/RetailCustomer/{retailCustomerId}/Authorization/{authorizationId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update Customer Authorization",
		description = "Updates an existing Authorization resource for a retail customer."
	)
	public void updateCustomerAuthorization(
			HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long authorizationId,
			@RequestParam Map<String, String> params, 
			@RequestBody InputStream stream) throws IOException, FeedException {
		
		try {
			Authorization authorization = authorizationService.findById(
					retailCustomerId, authorizationId);

			if (authorization != null) {
				Authorization newAuthorization = 
						authorizationService.importResource(stream);
				authorization.merge(newAuthorization);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@DeleteMapping("/RetailCustomer/{retailCustomerId}/Authorization/{authorizationId}")
	@Operation(
		summary = "Delete Customer Authorization", 
		description = "Removes an Authorization resource for a retail customer and revokes associated OAuth2 tokens."
	)
	public void deleteCustomerAuthorization(
			HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long authorizationId) {

		try {
			Authorization authorization = authorizationService.findById(
					retailCustomerId, authorizationId);
			
			if (authorization != null) {
				String accessToken = authorization.getAccessToken();
				authorizationService.delete(authorization);
				tokenService.revokeToken(accessToken);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}


}
