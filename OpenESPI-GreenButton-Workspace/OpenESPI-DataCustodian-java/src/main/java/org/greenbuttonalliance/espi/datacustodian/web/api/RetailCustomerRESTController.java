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
import org.greenbuttonalliance.espi.common.domain.usage.AuthorizationEntity;
import org.greenbuttonalliance.espi.common.domain.usage.RetailCustomerEntity;
import org.greenbuttonalliance.espi.common.domain.usage.SubscriptionEntity;
import org.greenbuttonalliance.espi.common.service.*;
import org.greenbuttonalliance.espi.common.utils.ExportFilter;
import org.greenbuttonalliance.espi.datacustodian.utils.VerifyURLParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * RESTful controller for managing RetailCustomer resources according to the 
 * Green Button Alliance ESPI (Energy Services Provider Interface) specification.
 * 
 * RetailCustomer represents the end consumer of utility services who may be 
 * either a person or organization.
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Retail Customer", description = "Utility Customer Account Management API")
public class RetailCustomerRESTController {

	private final ImportService importService;
	private final RetailCustomerService retailCustomerService;
	private final UsagePointService usagePointService;
	private final ExportService exportService;
	private final AuthorizationService authorizationService;

	@Autowired
	public RetailCustomerRESTController(
			ImportService importService,
			RetailCustomerService retailCustomerService,
			UsagePointService usagePointService,
			ExportService exportService,
			AuthorizationService authorizationService) {
		this.importService = importService;
		this.retailCustomerService = retailCustomerService;
		this.usagePointService = usagePointService;
		this.exportService = exportService;
		this.authorizationService = authorizationService;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
		// Generic exception handler
	}

	// ================================
	// ROOT RetailCustomer Collection APIs
	// ================================

	/**
	 * Retrieves all RetailCustomer resources (root level access).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for filtering and pagination
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM feed generation fails
	 */
	@GetMapping(value = "/RetailCustomer", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get RetailCustomer Collection",
		description = "Retrieves all authorized RetailCustomer resources with optional filtering and pagination. " +
					 "Returns an ATOM feed containing customer account information."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved RetailCustomer collection",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
							 schema = @Schema(description = "ATOM feed containing RetailCustomer entries"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid query parameters provided"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to customer data"
		)
	})
	public void getRetailCustomerCollection(
			HttpServletRequest request, 
			HttpServletResponse response,
			@Parameter(description = "Query parameters for filtering (published-max, published-min, updated-max, updated-min, max-results, start-index)")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/RetailCustomer", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportRetailCustomers(subscriptionId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a specific RetailCustomer resource by ID (root level access).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for streaming ATOM XML content
	 * @param retailCustomerId Unique identifier for the RetailCustomer
	 * @param params Query parameters for export filtering
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM entry generation fails
	 */
	@GetMapping(value = "/RetailCustomer/{retailCustomerId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get RetailCustomer by ID",
		description = "Retrieves a specific RetailCustomer resource by its unique identifier. " +
					 "Returns an ATOM entry containing customer account details and contact information."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved RetailCustomer",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing RetailCustomer details"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid retailCustomerId or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this customer data"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "RetailCustomer not found"
		)
	})
	public void getRetailCustomer(
			HttpServletRequest request, 
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the RetailCustomer", required = true)
			@PathVariable Long retailCustomerId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/RetailCustomer/{retailCustomerId}", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportRetailCustomer(subscriptionId,
					retailCustomerId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Creates a new RetailCustomer resource (root level).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for returning created resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 */
	@PostMapping(value = "/RetailCustomer", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create RetailCustomer",
		description = "Creates a new RetailCustomer resource representing a utility customer account. " +
					 "The request body should contain an ATOM entry with customer details including " +
					 "name, contact information, and account preferences."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Successfully created RetailCustomer",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing the created RetailCustomer"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or RetailCustomer data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to create customer accounts"
		)
	})
	public void createRetailCustomer(
			HttpServletRequest request,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing RetailCustomer data", required = true)
			@RequestBody InputStream stream) throws IOException {

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			RetailCustomerEntity retailCustomer = 
					this.retailCustomerService.importResource(stream);
			exportService.exportRetailCustomer(subscriptionId,
					retailCustomer.getId(), response.getOutputStream(),
					new ExportFilter(params));
			response.setStatus(HttpServletResponse.SC_CREATED);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Updates an existing RetailCustomer resource (root level).
	 * 
	 * @param response HTTP response for returning updated resource
	 * @param retailCustomerId Unique identifier for the RetailCustomer to update
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing updated ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 * @throws FeedException if ATOM processing fails
	 */
	@PutMapping(value = "/RetailCustomer/{retailCustomerId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update RetailCustomer",
		description = "Updates an existing RetailCustomer resource. The request body should contain " +
					 "an ATOM entry with updated customer account details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully updated RetailCustomer"
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or RetailCustomer data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to update this customer account"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "RetailCustomer not found"
		)
	})
	public void updateRetailCustomer(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the RetailCustomer to update", required = true)
			@PathVariable Long retailCustomerId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing updated RetailCustomer data", required = true)
			@RequestBody InputStream stream) throws IOException, FeedException {
		try {
			RetailCustomerEntity retailCustomer = 
					retailCustomerService.findById(retailCustomerId);

			if (retailCustomer != null) {
				RetailCustomer newRetailCustomer = 
						retailCustomerService.importResource(stream);
				retailCustomer.merge(newRetailCustomer);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Deletes a RetailCustomer resource (root level).
	 * 
	 * @param response HTTP response
	 * @param retailCustomerId Unique identifier for the RetailCustomer to delete
	 */
	@DeleteMapping("/RetailCustomer/{retailCustomerId}")
	@Operation(
		summary = "Delete RetailCustomer", 
		description = "Removes a RetailCustomer resource. This will also remove all associated " +
					 "usage points, authorizations, and subscription data."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully deleted RetailCustomer"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to delete this customer account"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "RetailCustomer not found"
		)
	})
	public void deleteRetailCustomer(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the RetailCustomer to delete", required = true)
			@PathVariable Long retailCustomerId) {

		try {
			RetailCustomerEntity retailCustomer = 
					retailCustomerService.findById(retailCustomerId);

			if (retailCustomer != null) {
				retailCustomerService.delete(retailCustomer);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	// =============================================
	// Utility Methods
	// =============================================

	/**
	 * Extracts subscription ID from the HTTP request context.
	 * 
	 * @param request HTTP servlet request
	 * @return Subscription ID if available, 0L otherwise
	 */
	private Long getSubscriptionId(HttpServletRequest request) {
		String token = request.getHeader("authorization");
		Long subscriptionId = 0L;

		if (token != null) {
			token = token.replace("Bearer ", "");
			AuthorizationEntity authorization = authorizationService.findByAccessToken(token);
			if (authorization != null) {
				SubscriptionEntity subscription = authorization.getSubscription();
				if (subscription != null) {
					subscriptionId = subscription.getId();
				}
			}
		}

		return subscriptionId;
	}


}
