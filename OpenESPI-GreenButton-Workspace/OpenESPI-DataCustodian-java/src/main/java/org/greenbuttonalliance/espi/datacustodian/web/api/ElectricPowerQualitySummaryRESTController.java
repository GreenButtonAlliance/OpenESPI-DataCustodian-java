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
import org.greenbuttonalliance.espi.common.domain.usage.ElectricPowerQualitySummaryEntity;
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
 * RESTful controller for managing ElectricPowerQualitySummary resources according to the 
 * Green Button Alliance ESPI (Energy Services Provider Interface) specification.
 * 
 * ElectricPowerQualitySummary represents power quality measurements and statistics 
 * including voltage, frequency, and harmonic distortion data.
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Electric Power Quality Summary", description = "Power Quality Measurement Data Management API")
public class ElectricPowerQualitySummaryRESTController {

	private final ElectricPowerQualitySummaryService electricPowerQualitySummaryService;
	private final UsagePointService usagePointService;
	private final RetailCustomerService retailCustomerService;
	private final ExportService exportService;
	private final ResourceService resourceService;
	private final SubscriptionService subscriptionService;
	private final AuthorizationService authorizationService;

	@Autowired
	public ElectricPowerQualitySummaryRESTController(
			ElectricPowerQualitySummaryService electricPowerQualitySummaryService,
			UsagePointService usagePointService,
			RetailCustomerService retailCustomerService,
			ExportService exportService,
			ResourceService resourceService,
			SubscriptionService subscriptionService,
			AuthorizationService authorizationService) {
		this.electricPowerQualitySummaryService = electricPowerQualitySummaryService;
		this.usagePointService = usagePointService;
		this.retailCustomerService = retailCustomerService;
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
	// ROOT ElectricPowerQualitySummary Collection APIs
	// ================================

	/**
	 * Retrieves all ElectricPowerQualitySummary resources (root level access).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for filtering and pagination
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM feed generation fails
	 */
	@GetMapping(value = "/ElectricPowerQualitySummary", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get ElectricPowerQualitySummary Collection",
		description = "Retrieves all authorized ElectricPowerQualitySummary resources with optional filtering and pagination. " +
					 "Returns an ATOM feed containing power quality measurement entries including voltage, frequency, and harmonic data."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved ElectricPowerQualitySummary collection",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
							 schema = @Schema(description = "ATOM feed containing ElectricPowerQualitySummary entries"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid query parameters provided"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to power quality data"
		)
	})
	public void getElectricPowerQualitySummaryCollection(
			HttpServletRequest request, 
			HttpServletResponse response,
			@Parameter(description = "Query parameters for filtering (published-max, published-min, updated-max, updated-min, max-results, start-index)")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/ElectricPowerQualitySummary", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportElectricPowerQualitySummarys_Root(subscriptionId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a specific ElectricPowerQualitySummary resource by ID (root level access).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for streaming ATOM XML content
	 * @param electricPowerQualitySummaryId Unique identifier for the ElectricPowerQualitySummary
	 * @param params Query parameters for export filtering
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM entry generation fails
	 */
	@GetMapping(value = "/ElectricPowerQualitySummary/{electricPowerQualitySummaryId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get ElectricPowerQualitySummary by ID",
		description = "Retrieves a specific ElectricPowerQualitySummary resource by its unique identifier. " +
					 "Returns an ATOM entry containing power quality details including voltage measurements, " +
					 "frequency variations, and harmonic distortion statistics."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved ElectricPowerQualitySummary",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing ElectricPowerQualitySummary details"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid electricPowerQualitySummaryId or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this power quality data"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "ElectricPowerQualitySummary not found"
		)
	})
	public void getElectricPowerQualitySummary(
			HttpServletRequest request, 
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the ElectricPowerQualitySummary", required = true)
			@PathVariable Long electricPowerQualitySummaryId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/ElectricPowerQualitySummary/{electricPowerQualitySummaryId}", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportElectricPowerQualitySummary_Root(
					subscriptionId, electricPowerQualitySummaryId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Creates a new ElectricPowerQualitySummary resource (root level).
	 * 
	 * @param request HTTP servlet request for authorization context
	 * @param response HTTP response for returning created resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 */
	@PostMapping(value = "/ElectricPowerQualitySummary", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create ElectricPowerQualitySummary",
		description = "Creates a new ElectricPowerQualitySummary resource representing power quality measurements. " +
					 "The request body should contain an ATOM entry with power quality details including " +
					 "voltage measurements, frequency data, and harmonic distortion statistics."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Successfully created ElectricPowerQualitySummary",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing the created ElectricPowerQualitySummary"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or ElectricPowerQualitySummary data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to create power quality data"
		)
	})
	public void createElectricPowerQualitySummary(
			HttpServletRequest request,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing ElectricPowerQualitySummary data", required = true)
			@RequestBody InputStream stream) throws IOException {

		Long subscriptionId = getSubscriptionId(request);
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			ElectricPowerQualitySummary electricPowerQualitySummary = 
					this.electricPowerQualitySummaryService.importResource(stream);
			exportService.exportElectricPowerQualitySummary_Root(
					subscriptionId, electricPowerQualitySummary.getId(),
					response.getOutputStream(), new ExportFilter(params));
			response.setStatus(HttpServletResponse.SC_CREATED);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Updates an existing ElectricPowerQualitySummary resource (root level).
	 * 
	 * @param response HTTP response for returning updated resource
	 * @param electricPowerQualitySummaryId Unique identifier for the ElectricPowerQualitySummary to update
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing updated ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 * @throws FeedException if ATOM processing fails
	 */
	@PutMapping(value = "/ElectricPowerQualitySummary/{electricPowerQualitySummaryId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update ElectricPowerQualitySummary",
		description = "Updates an existing ElectricPowerQualitySummary resource. The request body should contain " +
					 "an ATOM entry with updated power quality measurement details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully updated ElectricPowerQualitySummary"
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or ElectricPowerQualitySummary data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to update this power quality data"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "ElectricPowerQualitySummary not found"
		)
	})
	public void updateElectricPowerQualitySummary(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the ElectricPowerQualitySummary to update", required = true)
			@PathVariable Long electricPowerQualitySummaryId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing updated ElectricPowerQualitySummary data", required = true)
			@RequestBody InputStream stream) throws IOException, FeedException {
		try {
			ElectricPowerQualitySummary electricPowerQualitySummary = 
					electricPowerQualitySummaryService.findById(electricPowerQualitySummaryId);

			if (electricPowerQualitySummary != null) {
				ElectricPowerQualitySummary newElectricPowerQualitySummary = 
						electricPowerQualitySummaryService.importResource(stream);
				electricPowerQualitySummary.merge(newElectricPowerQualitySummary);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Deletes an ElectricPowerQualitySummary resource (root level).
	 * 
	 * @param response HTTP response
	 * @param electricPowerQualitySummaryId Unique identifier for the ElectricPowerQualitySummary to delete
	 */
	@DeleteMapping("/ElectricPowerQualitySummary/{electricPowerQualitySummaryId}")
	@Operation(
		summary = "Delete ElectricPowerQualitySummary", 
		description = "Removes an ElectricPowerQualitySummary resource. This will delete the " +
					 "power quality measurement data for the specified period."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully deleted ElectricPowerQualitySummary"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to delete this power quality data"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "ElectricPowerQualitySummary not found"
		)
	})
	public void deleteElectricPowerQualitySummary(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the ElectricPowerQualitySummary to delete", required = true)
			@PathVariable Long electricPowerQualitySummaryId) {

		try {
			resourceService.deleteById(electricPowerQualitySummaryId,
					ElectricPowerQualitySummary.class);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	// =============================================
	// Subscription-scoped ElectricPowerQualitySummary Collection APIs
	// =============================================

	/**
	 * Retrieves ElectricPowerQualitySummary resources within a specific subscription and usage point context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for filtering and pagination
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM feed generation fails
	 */
	@GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/ElectricPowerQualitySummary", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get ElectricPowerQualitySummaries by Subscription Context",
		description = "Retrieves all ElectricPowerQualitySummary resources associated with a specific subscription and usage point. " +
					 "This provides filtered access based on the subscription's authorization scope."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved subscription power quality summaries",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
							 schema = @Schema(description = "ATOM feed containing subscription-scoped ElectricPowerQualitySummary entries"))
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
	public void getSubscriptionElectricPowerQualitySummaries(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/ElectricPowerQualitySummary", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			exportService.exportElectricPowerQualitySummarys(subscriptionId,
					retailCustomerId, usagePointId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a specific ElectricPowerQualitySummary within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param electricPowerQualitySummaryId Unique identifier for the ElectricPowerQualitySummary
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for export filtering
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM entry generation fails
	 */
	@GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/ElectricPowerQualitySummary/{electricPowerQualitySummaryId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get Subscription ElectricPowerQualitySummary by ID",
		description = "Retrieves a specific ElectricPowerQualitySummary resource within a subscription context. " +
					 "This provides access control based on the subscription's authorization scope."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved subscription power quality summary",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing subscription-scoped ElectricPowerQualitySummary details"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid subscriptionId, usagePointId, electricPowerQualitySummaryId, or query parameters"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized access to this subscription or power quality data"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, or ElectricPowerQualitySummary not found"
		)
	})
	public void getSubscriptionElectricPowerQualitySummary(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the ElectricPowerQualitySummary", required = true)
			@PathVariable Long electricPowerQualitySummaryId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/ElectricPowerQualitySummary/{electricPowerQualitySummaryId}", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			exportService.exportElectricPowerQualitySummary(subscriptionId,
					retailCustomerId, usagePointId,
					electricPowerQualitySummaryId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Creates a new ElectricPowerQualitySummary resource within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param response HTTP response for returning created resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 */
	@PostMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/ElectricPowerQualitySummary", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create Subscription ElectricPowerQualitySummary",
		description = "Creates a new ElectricPowerQualitySummary resource within a subscription context. " +
					 "The request body should contain an ATOM entry with power quality measurement details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Successfully created ElectricPowerQualitySummary",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing the created ElectricPowerQualitySummary"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format, ElectricPowerQualitySummary data, or context"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to create power quality data in this context"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription or UsagePoint not found"
		)
	})
	public void createSubscriptionElectricPowerQualitySummary(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing ElectricPowerQualitySummary data", required = true)
			@RequestBody InputStream stream) throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			if (null != resourceService.findIdByXPath(retailCustomerId,
					usagePointId, UsagePoint.class)) {
				
				UsagePoint usagePoint = usagePointService.findById(usagePointId);
				ElectricPowerQualitySummary electricPowerQualitySummary = 
						electricPowerQualitySummaryService.importResource(stream);
				electricPowerQualitySummaryService.associateByUUID(usagePoint,
						electricPowerQualitySummary.getUUID());
				
				exportService.exportElectricPowerQualitySummary(subscriptionId,
						retailCustomerId, usagePointId,
						electricPowerQualitySummary.getId(),
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
	 * Updates an existing ElectricPowerQualitySummary resource within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param electricPowerQualitySummaryId Unique identifier for the ElectricPowerQualitySummary to update
	 * @param response HTTP response for returning updated resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing updated ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 * @throws FeedException if ATOM processing fails
	 */
	@PutMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/ElectricPowerQualitySummary/{electricPowerQualitySummaryId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update Subscription ElectricPowerQualitySummary",
		description = "Updates an existing ElectricPowerQualitySummary resource within a subscription context. " +
					 "The request body should contain an ATOM entry with updated power quality measurement details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully updated ElectricPowerQualitySummary"
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or ElectricPowerQualitySummary data"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to update this power quality data"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, or ElectricPowerQualitySummary not found"
		)
	})
	public void updateSubscriptionElectricPowerQualitySummary(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the ElectricPowerQualitySummary to update", required = true)
			@PathVariable Long electricPowerQualitySummaryId,
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing updated ElectricPowerQualitySummary data", required = true)
			@RequestBody InputStream stream) throws IOException, FeedException {

		try {
			ElectricPowerQualitySummary electricPowerQualitySummary = resourceService
					.findById(electricPowerQualitySummaryId, ElectricPowerQualitySummary.class);

			if (electricPowerQualitySummary != null) {
				electricPowerQualitySummary.merge(electricPowerQualitySummaryService.importResource(stream));
				resourceService.merge(electricPowerQualitySummary);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Deletes an ElectricPowerQualitySummary resource within a subscription context.
	 * 
	 * @param subscriptionId Unique identifier for the subscription
	 * @param usagePointId Unique identifier for the usage point
	 * @param electricPowerQualitySummaryId Unique identifier for the ElectricPowerQualitySummary to delete
	 * @param response HTTP response
	 */
	@DeleteMapping("/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/ElectricPowerQualitySummary/{electricPowerQualitySummaryId}")
	@Operation(
		summary = "Delete Subscription ElectricPowerQualitySummary", 
		description = "Removes an ElectricPowerQualitySummary resource within a subscription context. " +
					 "This will delete the power quality measurement data for the specified period."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully deleted ElectricPowerQualitySummary"
		),
		@ApiResponse(
			responseCode = "401", 
			description = "Unauthorized to delete this power quality data"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "Subscription, UsagePoint, or ElectricPowerQualitySummary not found"
		)
	})
	public void deleteSubscriptionElectricPowerQualitySummary(
			@Parameter(description = "Unique identifier of the subscription", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Unique identifier of the usage point", required = true)
			@PathVariable Long usagePointId,
			@Parameter(description = "Unique identifier of the ElectricPowerQualitySummary to delete", required = true)
			@PathVariable Long electricPowerQualitySummaryId,
			HttpServletResponse response) {

		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			resourceService.deleteByXPathId(retailCustomerId, usagePointId,
					electricPowerQualitySummaryId,
					ElectricPowerQualitySummary.class);
					
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
