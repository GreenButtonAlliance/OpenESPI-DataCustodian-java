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
import org.greenbuttonalliance.espi.common.domain.usage.MeterReadingEntity;
import org.greenbuttonalliance.espi.common.domain.usage.UsagePointEntity;
import org.greenbuttonalliance.espi.common.domain.usage.RetailCustomerEntity;
import org.greenbuttonalliance.espi.common.domain.usage.SubscriptionEntity;
import org.greenbuttonalliance.espi.common.domain.usage.IntervalBlockEntity;
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
 * RESTful controller for managing MeterReading resources according to the 
 * Green Button Alliance ESPI (Energy Services Provider Interface) specification.
 * 
 * MeterReading represents a collection of readings from a smart meter device 
 * for a specific time period, including actual consumption/production values 
 * and associated interval blocks.
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Meter Reading", description = "Smart Meter Reading Data Management API")
public class MeterReadingRESTController {

	private final MeterReadingService meterReadingService;
	private final UsagePointService usagePointService;
	private final RetailCustomerService retailCustomerService;
	private final SubscriptionService subscriptionService;
	private final ExportService exportService;
	private final ResourceService resourceService;
	private final AuthorizationService authorizationService;

	@Autowired
	public MeterReadingRESTController(
			MeterReadingService meterReadingService,
			UsagePointService usagePointService,
			RetailCustomerService retailCustomerService,
			SubscriptionService subscriptionService,
			ExportService exportService,
			ResourceService resourceService,
			AuthorizationService authorizationService) {
		this.meterReadingService = meterReadingService;
		this.usagePointService = usagePointService;
		this.retailCustomerService = retailCustomerService;
		this.subscriptionService = subscriptionService;
		this.exportService = exportService;
		this.resourceService = resourceService;
		this.authorizationService = authorizationService;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
		// Generic exception handler
	}

	// ================================
	// ROOT MeterReading Collection APIs
	// ================================

	/**
	 * Retrieves all MeterReading resources (root level access).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for filtering and pagination
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM feed generation fails
	 */
	@GetMapping(value = "/MeterReading", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get MeterReading Collection",
		description = "Retrieves all authorized MeterReading resources with optional filtering and pagination. " +
					 "Returns an ATOM feed containing MeterReading entries for smart meter data collections."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved MeterReading collection",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
							 schema = @Schema(description = "ATOM feed containing MeterReading entries"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid query parameters provided"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to MeterReading resources"
		)
	})
	public void getMeterReadingCollection(
			HttpServletRequest request, 
			HttpServletResponse response,
			@Parameter(description = "Query parameters for filtering (published-max, published-min, updated-max, updated-min, max-results, start-index)")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/MeterReading", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportMeterReadings_Root(subscriptionId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a specific MeterReading resource by ID (root level access).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for streaming ATOM XML content
	 * @param meterReadingId Unique identifier for the MeterReading
	 * @param params Query parameters for export filtering
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM entry generation fails
	 */
	@GetMapping(value = "/MeterReading/{meterReadingId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get MeterReading by ID",
		description = "Retrieves a specific MeterReading resource by its unique identifier. " +
					 "Returns an ATOM entry containing the MeterReading details including " +
					 "reading type, time period, and associated interval blocks."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved MeterReading",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing MeterReading details"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid meterReadingId or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this MeterReading"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "MeterReading not found"
		)
	})
	public void getMeterReading(
			HttpServletRequest request, 
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the MeterReading", required = true)
			@PathVariable Long meterReadingId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/MeterReading/{meterReadingId}", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportMeterReading_Root(subscriptionId,
					meterReadingId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Creates a new MeterReading resource (root level).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for returning created resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 */
	@PostMapping(value = "/MeterReading", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create MeterReading",
		description = "Creates a new MeterReading resource representing smart meter data collection. " +
					 "The request body should contain an ATOM entry with MeterReading details including " +
					 "reading type, time period, and interval block data."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Successfully created MeterReading",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing the created MeterReading"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or MeterReading data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to create MeterReadings"
		)
	})
	public void createMeterReading(
			HttpServletRequest request,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing MeterReading data", required = true)
			@RequestBody InputStream stream) throws IOException {

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			MeterReading meterReading = this.meterReadingService.importResource(stream);
			exportService.exportMeterReading_Root(subscriptionId,
					meterReading.getId(), response.getOutputStream(),
					new ExportFilter(params));
			response.setStatus(HttpServletResponse.SC_CREATED);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Updates an existing MeterReading resource (root level).
	 * 
	 * @param response HTTP response for returning updated resource
	 * @param meterReadingId Unique identifier for the MeterReading to update
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing updated ATOM XML data
	 */
	@PutMapping(value = "/MeterReading/{meterReadingId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update MeterReading",
		description = "Updates an existing MeterReading resource. The request body should contain " +
					 "an ATOM entry with updated MeterReading details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully updated MeterReading"
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or MeterReading data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to update this MeterReading"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "MeterReading not found"
		)
	})
	public void updateMeterReading(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the MeterReading to update", required = true)
			@PathVariable Long meterReadingId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing updated MeterReading data", required = true)
			@RequestBody InputStream stream) {

		if (null != resourceService.findById(meterReadingId, MeterReading.class)) {
			try {
				// Note: the import service is doing the merge
				meterReadingService.importResource(stream);
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * Deletes a MeterReading resource (root level).
	 * 
	 * @param response HTTP response
	 * @param meterReadingId Unique identifier for the MeterReading to delete
	 */
	@DeleteMapping("/MeterReading/{meterReadingId}")
	@Operation(
		summary = "Delete MeterReading", 
		description = "Removes a MeterReading resource. This will also remove all associated " +
					 "interval blocks and reading data."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully deleted MeterReading"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to delete this MeterReading"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "MeterReading not found"
		)
	})
	public void deleteMeterReading(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the MeterReading to delete", required = true)
			@PathVariable Long meterReadingId) {

		try {
			resourceService.deleteById(meterReadingId, MeterReading.class);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	// =============================================
	// Subscription-scoped MeterReading Collection APIs
	// =============================================

	/**
	 * Retrieves MeterReading resources within a specific subscription and usage point context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for filtering and pagination
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM feed generation fails
	 */
	@GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get MeterReadings by Subscription and UsagePoint",
		description = "Retrieves all MeterReading resources associated with a specific subscription and usage point. " +
					 "This provides filtered access based on the subscription's authorization scope."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved subscription MeterReadings",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
							 schema = @Schema(description = "ATOM feed containing subscription-scoped MeterReading entries"))
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
	public void getSubscriptionMeterReadings(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			exportService.exportMeterReadings(subscriptionId, retailCustomerId,
					usagePointId, response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a specific MeterReading within a subscription and usage point context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param meterReadingId Unique identifier for the MeterReading
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for export filtering
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM entry generation fails
	 */
	@GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get Subscription MeterReading by ID",
		description = "Retrieves a specific MeterReading resource within a subscription and usage point context. " +
					 "This provides access control based on the subscription's authorization scope."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved subscription MeterReading",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing subscription-scoped MeterReading details"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid subscriptionId, usagePointId, meterReadingId, or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this subscription or MeterReading"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, or MeterReading not found"
		)
	})
	public void getSubscriptionMeterReading(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the MeterReading", required = true)
			@PathVariable Long meterReadingId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);
			exportService.exportMeterReading(subscriptionId, retailCustomerId,
					usagePointId, meterReadingId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Creates a new MeterReading resource within a subscription and usage point context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param response HTTP response for returning created resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 */
	@PostMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create Subscription MeterReading",
		description = "Creates a new MeterReading resource within a subscription and usage point context. " +
					 "The request body should contain an ATOM entry with MeterReading details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Successfully created MeterReading",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing the created MeterReading"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format, MeterReading data, or context"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to create MeterReadings in this context"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription or UsagePoint not found"
		)
	})
	public void createSubscriptionMeterReading(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing MeterReading data", required = true)
			@RequestBody InputStream stream) throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			if (null != resourceService.findIdByXPath(retailCustomerId,
					usagePointId, UsagePoint.class)) {
				
				MeterReading meterReading = meterReadingService.importResource(stream);

				exportService.exportMeterReading(subscriptionId,
						retailCustomerId, usagePointId, meterReading.getId(),
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
	 * Updates an existing MeterReading resource within a subscription and usage point context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param meterReadingId Unique identifier for the MeterReading to update
	 * @param response HTTP response for returning updated resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing updated ATOM XML data
	 */
	@PutMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update Subscription MeterReading",
		description = "Updates an existing MeterReading resource within a subscription and usage point context. " +
					 "The request body should contain an ATOM entry with updated MeterReading details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully updated MeterReading"
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or MeterReading data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to update this MeterReading"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, or MeterReading not found"
		)
	})
	public void updateSubscriptionMeterReading(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the MeterReading to update", required = true)
			@PathVariable Long meterReadingId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing updated MeterReading data", required = true)
			@RequestBody InputStream stream) {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			if (null != resourceService.findIdByXPath(retailCustomerId,
					usagePointId, meterReadingId, MeterReading.class)) {
				
				meterReadingService.importResource(stream);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Deletes a MeterReading resource within a subscription and usage point context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param meterReadingId Unique identifier for the MeterReading to delete
	 * @param response HTTP response
	 */
	@DeleteMapping("/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}")
	@Operation(
		summary = "Delete Subscription MeterReading", 
		description = "Removes a MeterReading resource within a subscription and usage point context. " +
					 "This will also remove all associated interval blocks and reading data."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully deleted MeterReading"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to delete this MeterReading"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, or MeterReading not found"
		)
	})
	public void deleteSubscriptionMeterReading(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the MeterReading to delete", required = true)
			@PathVariable Long meterReadingId,
			HttpServletResponse response) {

		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			resourceService.deleteByXPathId(retailCustomerId, usagePointId,
					meterReadingId, MeterReading.class);
					
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
