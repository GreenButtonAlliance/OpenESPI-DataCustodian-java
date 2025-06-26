/*
 *
 *    Copyright (c) 2018-2025 Green Button Alliance, Inc.
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
import org.greenbuttonalliance.espi.common.domain.usage.UsageSummaryEntity;
import org.greenbuttonalliance.espi.common.domain.usage.UsagePointEntity;
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
import java.util.Map;

/**
 * RESTful controller for managing UsageSummary resources according to the 
 * Green Button Alliance ESPI (Energy Services Provider Interface) specification.
 * 
 * UsageSummary represents aggregated utility usage data over specific time periods,
 * including totals for consumption, production, and billing determinant values.
 * Supports electricity, water, natural gas, and other utility commodities.
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Usage Summary", description = "Multi-Commodity Usage Summary Data Management API")
public class UsageSummaryRESTController {

	private final UsageSummaryService usageSummaryService;
	private final UsagePointService usagePointService;
	private final ExportService exportService;
	private final ResourceService resourceService;
	private final SubscriptionService subscriptionService;
	private final AuthorizationService authorizationService;

	@Autowired
	public UsageSummaryRESTController(
			UsageSummaryService usageSummaryService,
			UsagePointService usagePointService,
			ExportService exportService,
			ResourceService resourceService,
			SubscriptionService subscriptionService,
			AuthorizationService authorizationService) {
		this.usageSummaryService = usageSummaryService;
		this.usagePointService = usagePointService;
		this.exportService = exportService;
		this.resourceService = resourceService;
		this.subscriptionService = subscriptionService;
		this.authorizationService = authorizationService;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
		// Generic exception handler
	}

	// ================================
	// ROOT UsageSummary Collection APIs
	// ================================

	/**
	 * Retrieves all UsageSummary resources (root level access).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for filtering and pagination
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM feed generation fails
	 */
	@GetMapping(value = "/UsageSummary", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get UsageSummary Collection",
		description = "Retrieves all authorized UsageSummary resources with optional filtering and pagination. " +
					 "Returns an ATOM feed containing usage summary entries for multi-commodity consumption and production data."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved UsageSummary collection",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
							 schema = @Schema(description = "ATOM feed containing UsageSummary entries"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid query parameters provided"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to usage summary resources"
		)
	})
	public void getUsageSummaryCollection(
			HttpServletRequest request, 
			HttpServletResponse response,
			@Parameter(description = "Query parameters for filtering (published-max, published-min, updated-max, updated-min, max-results, start-index)")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/UsageSummary", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportUsageSummarys_Root(subscriptionId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a specific UsageSummary resource by ID (root level access).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for streaming ATOM XML content
	 * @param electricPowerUsageSummaryId Unique identifier for the UsageSummary
	 * @param params Query parameters for export filtering
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM entry generation fails
	 */
	@GetMapping(value = "/UsageSummary/{electricPowerUsageSummaryId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get UsageSummary by ID",
		description = "Retrieves a specific UsageSummary resource by its unique identifier. " +
					 "Returns an ATOM entry containing the usage summary details including " +
					 "billing period, total consumption, total production, and cost information."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved UsageSummary",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing UsageSummary details"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid electricPowerUsageSummaryId or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this usage summary"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "UsageSummary not found"
		)
	})
	public void getUsageSummary(
			HttpServletRequest request, 
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the UsageSummary", required = true)
			@PathVariable Long electricPowerUsageSummaryId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/UsageSummary/{electricPowerUsageSummaryId}", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportUsageSummary_Root(subscriptionId,
					electricPowerUsageSummaryId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Creates a new UsageSummary resource (root level).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for returning created resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 */
	@PostMapping(value = "/UsageSummary", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create UsageSummary",
		description = "Creates a new UsageSummary resource representing aggregated electricity usage data. " +
					 "The request body should contain an ATOM entry with usage summary details including " +
					 "billing period, consumption totals, and cost information."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Successfully created UsageSummary",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing the created UsageSummary"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or UsageSummary data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to create usage summaries"
		)
	})
	public void createUsageSummary(
			HttpServletRequest request,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing UsageSummary data", required = true)
			@RequestBody InputStream stream) throws IOException {

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			UsageSummary electricPowerUsageSummary = 
					this.usageSummaryService.importResource(stream);
			exportService.exportUsageSummary_Root(subscriptionId,
					electricPowerUsageSummary.getId(),
					response.getOutputStream(), new ExportFilter(params));
			response.setStatus(HttpServletResponse.SC_CREATED);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Updates an existing UsageSummary resource (root level).
	 * 
	 * @param response HTTP response for returning updated resource
	 * @param electricPowerUsageSummaryId Unique identifier for the UsageSummary to update
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing updated ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 * @throws FeedException if ATOM processing fails
	 */
	@PutMapping(value = "/UsageSummary/{electricPowerUsageSummaryId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update UsageSummary",
		description = "Updates an existing UsageSummary resource. The request body should contain " +
					 "an ATOM entry with updated usage summary details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully updated UsageSummary"
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or UsageSummary data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to update this usage summary"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "UsageSummary not found"
		)
	})
	public void updateUsageSummary(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the UsageSummary to update", required = true)
			@PathVariable Long electricPowerUsageSummaryId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing updated UsageSummary data", required = true)
			@RequestBody InputStream stream) throws IOException, FeedException {

		UsageSummary electricPowerUsageSummary = 
				usageSummaryService.findById(electricPowerUsageSummaryId);

		if (electricPowerUsageSummary != null) {
			try {
				UsageSummary newUsageSummary = 
						usageSummaryService.importResource(stream);
				electricPowerUsageSummary.merge(newUsageSummary);
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * Deletes an UsageSummary resource (root level).
	 * 
	 * @param response HTTP response
	 * @param electricPowerUsageSummaryId Unique identifier for the UsageSummary to delete
	 */
	@DeleteMapping("/UsageSummary/{electricPowerUsageSummaryId}")
	@Operation(
		summary = "Delete UsageSummary", 
		description = "Removes an UsageSummary resource. This will delete the " +
					 "aggregated usage data for the specified billing period."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully deleted UsageSummary"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to delete this usage summary"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "UsageSummary not found"
		)
	})
	public void deleteUsageSummary(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the UsageSummary to delete", required = true)
			@PathVariable Long electricPowerUsageSummaryId) {

		try {
			resourceService.deleteById(electricPowerUsageSummaryId,
					UsageSummary.class);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	// =============================================
	// Subscription-scoped UsageSummary Collection APIs
	// =============================================

	/**
	 * Retrieves UsageSummary resources within a specific subscription and usage point context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for filtering and pagination
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM feed generation fails
	 */
	@GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/UsageSummary", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get UsageSummaries by Subscription Context",
		description = "Retrieves all UsageSummary resources associated with a specific subscription and usage point. " +
					 "This provides filtered access based on the subscription's authorization scope."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved subscription usage summaries",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
							 schema = @Schema(description = "ATOM feed containing subscription-scoped UsageSummary entries"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid subscriptionId, usagePointId, or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this subscription"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription or UsagePoint not found"
		)
	})
	public void getSubscriptionUsageSummaries(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/UsageSummary", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			exportService.exportUsageSummarys(subscriptionId,
					retailCustomerId, usagePointId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a specific UsageSummary within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param electricPowerUsageSummaryId Unique identifier for the UsageSummary
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for export filtering
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM entry generation fails
	 */
	@GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/UsageSummary/{electricPowerUsageSummaryId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get Subscription UsageSummary by ID",
		description = "Retrieves a specific UsageSummary resource within a subscription context. " +
					 "This provides access control based on the subscription's authorization scope."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved subscription usage summary",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing subscription-scoped UsageSummary details"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid subscriptionId, usagePointId, electricPowerUsageSummaryId, or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this subscription or usage summary"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, or UsageSummary not found"
		)
	})
	public void getSubscriptionUsageSummary(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the UsageSummary", required = true)
			@PathVariable Long electricPowerUsageSummaryId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			exportService.exportUsageSummary(subscriptionId,
					retailCustomerId, usagePointId,
					electricPowerUsageSummaryId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Creates a new UsageSummary resource within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param response HTTP response for returning created resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 */
	@PostMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/UsageSummary", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create Subscription UsageSummary",
		description = "Creates a new UsageSummary resource within a subscription context. " +
					 "The request body should contain an ATOM entry with usage summary details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Successfully created UsageSummary",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing the created UsageSummary"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format, UsageSummary data, or context"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to create usage summaries in this context"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription or UsagePoint not found"
		)
	})
	public void createSubscriptionUsageSummary(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing UsageSummary data", required = true)
			@RequestBody InputStream stream) throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			if (null != resourceService.findIdByXPath(retailCustomerId,
					usagePointId, UsagePoint.class)) {
				
				UsagePoint usagePoint = usagePointService.findById(usagePointId);
				UsageSummary electricPowerUsageSummary = 
						this.usageSummaryService.importResource(stream);
				usageSummaryService.associateByUUID(usagePoint,
						electricPowerUsageSummary.getUUID());
				
				exportService.exportUsageSummary(subscriptionId,
						retailCustomerId, usagePointId,
						electricPowerUsageSummary.getId(),
						response.getOutputStream(), new ExportFilter(params));
						
				response.setStatus(HttpServletResponse.SC_CREATED);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Updates an existing UsageSummary resource within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param electricPowerUsageSummaryId Unique identifier for the UsageSummary to update
	 * @param response HTTP response for returning updated resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing updated ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 * @throws FeedException if ATOM processing fails
	 */
	@PutMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/UsageSummary/{electricPowerUsageSummaryId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update Subscription UsageSummary",
		description = "Updates an existing UsageSummary resource within a subscription context. " +
					 "The request body should contain an ATOM entry with updated usage summary details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully updated UsageSummary"
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or UsageSummary data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to update this usage summary"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, or UsageSummary not found"
		)
	})
	public void updateSubscriptionUsageSummary(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the UsageSummary to update", required = true)
			@PathVariable Long electricPowerUsageSummaryId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing updated UsageSummary data", required = true)
			@RequestBody InputStream stream) throws IOException, FeedException {

		try {
			UsageSummary electricPowerUsageSummary = resourceService
					.findById(electricPowerUsageSummaryId, UsageSummary.class);

			if (electricPowerUsageSummary != null) {
				electricPowerUsageSummary.merge(usageSummaryService.importResource(stream));
				resourceService.merge(electricPowerUsageSummary);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Deletes an UsageSummary resource within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param electricPowerUsageSummaryId Unique identifier for the UsageSummary to delete
	 * @param response HTTP response
	 */
	@DeleteMapping("/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/UsageSummary/{electricPowerUsageSummaryId}")
	@Operation(
		summary = "Delete Subscription UsageSummary", 
		description = "Removes an UsageSummary resource within a subscription context. " +
					 "This will delete the aggregated usage data for the specified billing period."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully deleted UsageSummary"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to delete this usage summary"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, or UsageSummary not found"
		)
	})
	public void deleteSubscriptionUsageSummary(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the UsageSummary to delete", required = true)
			@PathVariable Long electricPowerUsageSummaryId,
			HttpServletResponse response) {

		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			resourceService.deleteByXPathId(retailCustomerId, usagePointId,
					electricPowerUsageSummaryId, UsageSummary.class);
					
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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
			Authorization authorization = authorizationService.findByAccessToken(token);
			if (authorization != null) {
				Subscription subscription = authorization.getSubscription();
				if (subscription != null) {
					subscriptionId = subscription.getId();
				}
			}
		}

		return subscriptionId;
	}


}
