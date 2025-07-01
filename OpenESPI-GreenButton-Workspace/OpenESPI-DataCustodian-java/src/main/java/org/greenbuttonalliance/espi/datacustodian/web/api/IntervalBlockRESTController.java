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
import org.greenbuttonalliance.espi.common.domain.usage.IntervalBlockEntity;
import org.greenbuttonalliance.espi.common.domain.usage.MeterReadingEntity;
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
 * RESTful controller for managing IntervalBlock resources according to the 
 * Green Button Alliance ESPI (Energy Services Provider Interface) specification.
 * 
 * IntervalBlock represents a collection of interval readings over a specific 
 * time period, containing the actual energy consumption or production values
 * recorded by smart meters at regular intervals.
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Interval Block", description = "Smart Meter Interval Data Management API")
public class IntervalBlockRESTController {

	private final IntervalBlockService intervalBlockService;
	private final RetailCustomerService retailCustomerService;
	private final UsagePointService usagePointService;
	private final MeterReadingService meterReadingService;
	private final ExportService exportService;
	private final ResourceService resourceService;
	private final SubscriptionService subscriptionService;
	private final AuthorizationService authorizationService;

	@Autowired
	public IntervalBlockRESTController(
			IntervalBlockService intervalBlockService,
			RetailCustomerService retailCustomerService,
			UsagePointService usagePointService,
			MeterReadingService meterReadingService,
			ExportService exportService,
			ResourceService resourceService,
			SubscriptionService subscriptionService,
			AuthorizationService authorizationService) {
		this.intervalBlockService = intervalBlockService;
		this.retailCustomerService = retailCustomerService;
		this.usagePointService = usagePointService;
		this.meterReadingService = meterReadingService;
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
	// ROOT IntervalBlock Collection APIs
	// ================================

	/**
	 * Retrieves all IntervalBlock resources (root level access).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for filtering and pagination
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM feed generation fails
	 */
	@GetMapping(value = "/IntervalBlock", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get IntervalBlock Collection",
		description = "Retrieves all authorized IntervalBlock resources with optional filtering and pagination. " +
					 "Returns an ATOM feed containing IntervalBlock entries for smart meter interval data."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved IntervalBlock collection",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
							 schema = @Schema(description = "ATOM feed containing IntervalBlock entries"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid query parameters provided"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to IntervalBlock resources"
		)
	})
	public void getIntervalBlockCollection(
			HttpServletRequest request, 
			HttpServletResponse response,
			@Parameter(description = "Query parameters for filtering (published-max, published-min, updated-max, updated-min, max-results, start-index)")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/IntervalBlock", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportIntervalBlocks_Root(subscriptionId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a specific IntervalBlock resource by ID (root level access).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for streaming ATOM XML content
	 * @param intervalBlockId Unique identifier for the IntervalBlock
	 * @param params Query parameters for export filtering
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM entry generation fails
	 */
	@GetMapping(value = "/IntervalBlock/{intervalBlockId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get IntervalBlock by ID",
		description = "Retrieves a specific IntervalBlock resource by its unique identifier. " +
					 "Returns an ATOM entry containing the IntervalBlock details including " +
					 "time period, reading values, and interval reading data."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved IntervalBlock",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing IntervalBlock details"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid intervalBlockId or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this IntervalBlock"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "IntervalBlock not found"
		)
	})
	public void getIntervalBlock(
			HttpServletRequest request, 
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the IntervalBlock", required = true)
			@PathVariable Long intervalBlockId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/IntervalBlock/{intervalBlockId}", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportIntervalBlock_Root(subscriptionId,
					intervalBlockId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Creates a new IntervalBlock resource (root level).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for returning created resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 */
	@PostMapping(value = "/IntervalBlock", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create IntervalBlock",
		description = "Creates a new IntervalBlock resource representing smart meter interval data. " +
					 "The request body should contain an ATOM entry with IntervalBlock details including " +
					 "time period, reading values, and interval reading data."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Successfully created IntervalBlock",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing the created IntervalBlock"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or IntervalBlock data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to create IntervalBlocks"
		)
	})
	public void createIntervalBlock(
			HttpServletRequest request,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing IntervalBlock data", required = true)
			@RequestBody InputStream stream) throws IOException {

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			IntervalBlock intervalBlock = this.intervalBlockService.importResource(stream);
			exportService.exportIntervalBlock_Root(subscriptionId,
					intervalBlock.getId(), response.getOutputStream(),
					new ExportFilter(params));
			response.setStatus(HttpServletResponse.SC_CREATED);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Updates an existing IntervalBlock resource (root level).
	 * 
	 * @param response HTTP response for returning updated resource
	 * @param intervalBlockId Unique identifier for the IntervalBlock to update
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing updated ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 * @throws FeedException if ATOM processing fails
	 */
	@PutMapping(value = "/IntervalBlock/{intervalBlockId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update IntervalBlock",
		description = "Updates an existing IntervalBlock resource. The request body should contain " +
					 "an ATOM entry with updated IntervalBlock details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully updated IntervalBlock"
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or IntervalBlock data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to update this IntervalBlock"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "IntervalBlock not found"
		)
	})
	public void updateIntervalBlock(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the IntervalBlock to update", required = true)
			@PathVariable Long intervalBlockId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing updated IntervalBlock data", required = true)
			@RequestBody InputStream stream) throws IOException, FeedException {

		IntervalBlock intervalBlock = intervalBlockService.findById(intervalBlockId);

		if (intervalBlock != null) {
			try {
				intervalBlock.merge(intervalBlockService.importResource(stream));
				intervalBlockService.persist(intervalBlock);
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * Deletes an IntervalBlock resource (root level).
	 * 
	 * @param response HTTP response
	 * @param intervalBlockId Unique identifier for the IntervalBlock to delete
	 */
	@DeleteMapping("/IntervalBlock/{intervalBlockId}")
	@Operation(
		summary = "Delete IntervalBlock", 
		description = "Removes an IntervalBlock resource. This will delete all associated " +
					 "interval reading data and timestamps."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully deleted IntervalBlock"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to delete this IntervalBlock"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "IntervalBlock not found"
		)
	})
	public void deleteIntervalBlock(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the IntervalBlock to delete", required = true)
			@PathVariable Long intervalBlockId) {

		try {
			resourceService.deleteById(intervalBlockId, IntervalBlock.class);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	// =============================================
	// Subscription-scoped IntervalBlock Collection APIs
	// =============================================

	/**
	 * Retrieves IntervalBlock resources within a specific subscription, usage point, and meter reading context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param meterReadingId Unique identifier for the meter reading
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for filtering and pagination
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM feed generation fails
	 */
	@GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}/IntervalBlock", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get IntervalBlocks by Subscription Context",
		description = "Retrieves all IntervalBlock resources associated with a specific subscription, usage point, and meter reading. " +
					 "This provides filtered access based on the subscription's authorization scope."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved subscription IntervalBlocks",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
							 schema = @Schema(description = "ATOM feed containing subscription-scoped IntervalBlock entries"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid subscriptionId, usagePointId, meterReadingId, or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this subscription"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, or MeterReading not found"
		)
	})
	public void getSubscriptionIntervalBlocks(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the meter reading", required = true)
			@PathVariable Long meterReadingId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}/IntervalBlock", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			exportService.exportIntervalBlocks(subscriptionId, retailCustomerId,
					usagePointId, meterReadingId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a specific IntervalBlock within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param meterReadingId Unique identifier for the meter reading
	 * @param intervalBlockId Unique identifier for the IntervalBlock
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for export filtering
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM entry generation fails
	 */
	@GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}/IntervalBlock/{intervalBlockId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get Subscription IntervalBlock by ID",
		description = "Retrieves a specific IntervalBlock resource within a subscription context. " +
					 "This provides access control based on the subscription's authorization scope."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved subscription IntervalBlock",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing subscription-scoped IntervalBlock details"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid subscriptionId, usagePointId, meterReadingId, intervalBlockId, or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this subscription or IntervalBlock"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, MeterReading, or IntervalBlock not found"
		)
	})
	public void getSubscriptionIntervalBlock(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the meter reading", required = true)
			@PathVariable Long meterReadingId,
			@Parameter(description = "Unique identifier of the IntervalBlock", required = true)
			@PathVariable Long intervalBlockId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			exportService.exportIntervalBlock(subscriptionId, retailCustomerId,
					usagePointId, meterReadingId, intervalBlockId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Creates a new IntervalBlock resource within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param meterReadingId Unique identifier for the meter reading
	 * @param response HTTP response for returning created resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 */
	@PostMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}/IntervalBlock", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create Subscription IntervalBlock",
		description = "Creates a new IntervalBlock resource within a subscription context. " +
					 "The request body should contain an ATOM entry with IntervalBlock details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Successfully created IntervalBlock",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing the created IntervalBlock"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format, IntervalBlock data, or context"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to create IntervalBlocks in this context"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, or MeterReading not found"
		)
	})
	public void createSubscriptionIntervalBlock(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the meter reading", required = true)
			@PathVariable Long meterReadingId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing IntervalBlock data", required = true)
			@RequestBody InputStream stream) throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			if (null != resourceService.findIdByXPath(retailCustomerId,
					usagePointId, meterReadingId, MeterReading.class)) {
				
				MeterReading meterReading = resourceService.findById(
						meterReadingId, MeterReading.class);
				IntervalBlock intervalBlock = this.intervalBlockService.importResource(stream);
				intervalBlockService.associateByUUID(meterReading, intervalBlock.getUUID());
				
				exportService.exportIntervalBlock(subscriptionId,
						retailCustomerId, usagePointId, meterReadingId,
						intervalBlock.getId(), response.getOutputStream(),
						new ExportFilter(params));
						
				response.setStatus(HttpServletResponse.SC_CREATED);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Updates an existing IntervalBlock resource within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param meterReadingId Unique identifier for the meter reading
	 * @param intervalBlockId Unique identifier for the IntervalBlock to update
	 * @param response HTTP response for returning updated resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing updated ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 * @throws FeedException if ATOM processing fails
	 */
	@PutMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}/IntervalBlock/{intervalBlockId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update Subscription IntervalBlock",
		description = "Updates an existing IntervalBlock resource within a subscription context. " +
					 "The request body should contain an ATOM entry with updated IntervalBlock details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully updated IntervalBlock"
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or IntervalBlock data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to update this IntervalBlock"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, MeterReading, or IntervalBlock not found"
		)
	})
	public void updateSubscriptionIntervalBlock(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the meter reading", required = true)
			@PathVariable Long meterReadingId,
			@Parameter(description = "Unique identifier of the IntervalBlock to update", required = true)
			@PathVariable Long intervalBlockId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing updated IntervalBlock data", required = true)
			@RequestBody InputStream stream) throws IOException, FeedException {

		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			IntervalBlock intervalBlock = intervalBlockService.findById(retailCustomerId, 
					usagePointId, meterReadingId, intervalBlockId);

			if (intervalBlock != null) {
				intervalBlock.merge(intervalBlockService.importResource(stream));
				resourceService.merge(intervalBlock);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Deletes an IntervalBlock resource within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param meterReadingId Unique identifier for the meter reading
	 * @param intervalBlockId Unique identifier for the IntervalBlock to delete
	 * @param response HTTP response
	 */
	@DeleteMapping("/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}/IntervalBlock/{intervalBlockId}")
	@Operation(
		summary = "Delete Subscription IntervalBlock", 
		description = "Removes an IntervalBlock resource within a subscription context. " +
					 "This will delete all associated interval reading data and timestamps."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully deleted IntervalBlock"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to delete this IntervalBlock"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, MeterReading, or IntervalBlock not found"
		)
	})
	public void deleteSubscriptionIntervalBlock(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the meter reading", required = true)
			@PathVariable Long meterReadingId,
			@Parameter(description = "Unique identifier of the IntervalBlock to delete", required = true)
			@PathVariable Long intervalBlockId,
			HttpServletResponse response) {

		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			resourceService.deleteByXPathId(retailCustomerId, usagePointId,
					meterReadingId, intervalBlockId, IntervalBlock.class);
					
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
