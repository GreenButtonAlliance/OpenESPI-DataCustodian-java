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
import org.greenbuttonalliance.espi.common.domain.usage.UsagePointEntity;
import org.greenbuttonalliance.espi.common.domain.usage.RetailCustomerEntity;
import org.greenbuttonalliance.espi.common.domain.usage.SubscriptionEntity;
import org.greenbuttonalliance.espi.common.domain.usage.MeterReadingEntity;
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
 * RESTful controller for managing UsagePoint resources according to the 
 * Green Button Alliance ESPI (Energy Services Provider Interface) specification.
 * 
 * UsagePoint represents a logical point on the network where consumption or 
 * production is measured (e.g., meter connection point, sub-load, or generation source).
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Usage Point", description = "Smart Meter Usage Point Management API")
public class UsagePointRESTController {

    private final UsagePointService usagePointService;
    private final SubscriptionService subscriptionService;
    private final RetailCustomerService retailCustomerService;
    private final ExportService exportService;
    private final ResourceService resourceService;
    private final AuthorizationService authorizationService;

    @Autowired
    public UsagePointRESTController(
            UsagePointService usagePointService,
            SubscriptionService subscriptionService,
            RetailCustomerService retailCustomerService,
            ExportService exportService,
            ResourceService resourceService,
            AuthorizationService authorizationService) {
        this.usagePointService = usagePointService;
        this.subscriptionService = subscriptionService;
        this.retailCustomerService = retailCustomerService;
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
    // ROOT UsagePoint Collection APIs
    // ================================

    /**
     * Retrieves all UsagePoint resources (root level access).
     * 
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response for streaming ATOM XML content
     * @param params Query parameters for filtering and pagination
     * @throws IOException if output stream cannot be written
     * @throws FeedException if ATOM feed generation fails
     */
    @GetMapping(value = "/UsagePoint", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @Operation(
        summary = "Get UsagePoint Collection",
        description = "Retrieves all authorized UsagePoint resources with optional filtering and pagination. " +
                     "Returns an ATOM feed containing UsagePoint entries for smart meter connection points."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved UsagePoint collection",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
                             schema = @Schema(description = "ATOM feed containing UsagePoint entries"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid query parameters provided"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized access to UsagePoint resources"
        )
    })
    public void getUsagePointCollection(
            HttpServletRequest request, 
            HttpServletResponse response,
            @Parameter(description = "Query parameters for filtering (published-max, published-min, updated-max, updated-min, max-results, start-index)")
            @RequestParam Map<String, String> params) throws IOException, FeedException {

        // Verify request contains valid query parameters
        if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/UsagePoint", params)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                             "Request contains invalid query parameter values!");
            return;
        }

        Long subscriptionId = getSubscriptionId(request);
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        
        try {
            exportService.exportUsagePoints_Root(subscriptionId,
                    response.getOutputStream(), new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Retrieves a specific UsagePoint resource by ID (root level access).
     * 
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response for streaming ATOM XML content
     * @param usagePointId Unique identifier for the UsagePoint
     * @param params Query parameters for export filtering
     * @throws IOException if output stream cannot be written
     * @throws FeedException if ATOM entry generation fails
     */
    @GetMapping(value = "/UsagePoint/{usagePointId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @Operation(
        summary = "Get UsagePoint by ID",
        description = "Retrieves a specific UsagePoint resource by its unique identifier. " +
                     "Returns an ATOM entry containing the UsagePoint details including service category, " +
                     "connection state, and meter configuration."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved UsagePoint",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
                             schema = @Schema(description = "ATOM entry containing UsagePoint details"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid usagePointId or query parameters"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized access to this UsagePoint"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "UsagePoint not found"
        )
    })
    public void getUsagePoint(
            HttpServletRequest request, 
            HttpServletResponse response,
            @Parameter(description = "Unique identifier of the UsagePoint", required = true)
            @PathVariable Long usagePointId,
            @Parameter(description = "Query parameters for export filtering")
            @RequestParam Map<String, String> params) throws IOException, FeedException {

        // Verify request contains valid query parameters
        if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/UsagePoint/{usagePointId}", params)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                             "Request contains invalid query parameter values!");
            return;
        }

        Long subscriptionId = getSubscriptionId(request);
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        
        try {
            exportService.exportUsagePoint_Root(subscriptionId, usagePointId,
                    response.getOutputStream(), new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Creates a new UsagePoint resource (root level).
     * 
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response for returning created resource
     * @param params Query parameters for export filtering
     * @param stream Input stream containing ATOM XML data
     * @throws IOException if input/output stream operations fail
     */
    @PostMapping(value = "/UsagePoint", 
                consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
                produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @Operation(
        summary = "Create UsagePoint",
        description = "Creates a new UsagePoint resource representing a smart meter connection point. " +
                     "The request body should contain an ATOM entry with UsagePoint details including " +
                     "service category, connection state, and meter identification."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Successfully created UsagePoint",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
                             schema = @Schema(description = "ATOM entry containing the created UsagePoint"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid ATOM XML format or UsagePoint data"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized to create UsagePoints"
        )
    })
    public void createUsagePoint(
            HttpServletRequest request, 
            HttpServletResponse response,
            @Parameter(description = "Query parameters for export filtering")
            @RequestParam Map<String, String> params,
            @Parameter(description = "ATOM XML containing UsagePoint data", required = true)
            @RequestBody InputStream stream) throws IOException {

        Long subscriptionId = getSubscriptionId(request);
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        
        try {
            UsagePoint usagePoint = this.usagePointService.importResource(stream);
            exportService.exportUsagePoint_Root(subscriptionId, usagePoint.getId(),
                    response.getOutputStream(), new ExportFilter(params));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Updates an existing UsagePoint resource (root level).
     * 
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response for returning updated resource
     * @param usagePointId Unique identifier for the UsagePoint to update
     * @param params Query parameters for export filtering
     * @param stream Input stream containing updated ATOM XML data
     * @throws IOException if input/output stream operations fail
     */
    @PutMapping(value = "/UsagePoint/{usagePointId}", 
               consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
    @Operation(
        summary = "Update UsagePoint",
        description = "Updates an existing UsagePoint resource. The request body should contain " +
                     "an ATOM entry with updated UsagePoint details."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully updated UsagePoint"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid ATOM XML format or UsagePoint data"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized to update this UsagePoint"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "UsagePoint not found"
        )
    })
    public void updateUsagePoint(
            HttpServletRequest request, 
            HttpServletResponse response,
            @Parameter(description = "Unique identifier of the UsagePoint to update", required = true)
            @PathVariable Long usagePointId,
            @Parameter(description = "Query parameters for export filtering")
            @RequestParam Map<String, String> params,
            @Parameter(description = "ATOM XML containing updated UsagePoint data", required = true)
            @RequestBody InputStream stream) throws IOException {

        try {
            UsagePoint usagePoint = usagePointService.importResource(stream);
            usagePoint.setId(usagePointId);
            usagePointService.save(usagePoint);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Deletes a UsagePoint resource (root level).
     * 
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response
     * @param usagePointId Unique identifier for the UsagePoint to delete
     */
    @DeleteMapping("/UsagePoint/{usagePointId}")
    @Operation(
        summary = "Delete UsagePoint", 
        description = "Removes a UsagePoint resource. This will also remove all associated " +
                     "meter readings, interval blocks, and usage summaries."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully deleted UsagePoint"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized to delete this UsagePoint"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "UsagePoint not found"
        )
    })
    public void deleteUsagePoint(
            HttpServletRequest request, 
            HttpServletResponse response,
            @Parameter(description = "Unique identifier of the UsagePoint to delete", required = true)
            @PathVariable Long usagePointId) {

        try {
            usagePointService.deleteById(usagePointId);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // =============================================
    // Subscription-scoped UsagePoint Collection APIs
    // =============================================

    /**
     * Retrieves UsagePoint resources within a specific subscription context.
     * 
     * @param subscriptionId Unique identifier for the subscription
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response for streaming ATOM XML content
     * @param params Query parameters for filtering and pagination
     * @throws IOException if output stream cannot be written
     * @throws FeedException if ATOM feed generation fails
     */
    @GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @Operation(
        summary = "Get UsagePoints by Subscription",
        description = "Retrieves all UsagePoint resources associated with a specific subscription. " +
                     "This provides filtered access based on the subscription's authorization scope."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved subscription UsagePoints",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
                             schema = @Schema(description = "ATOM feed containing subscription-scoped UsagePoint entries"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid subscriptionId or query parameters"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized access to this subscription"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Subscription not found"
        )
    })
    public void getSubscriptionUsagePoints(
            @Parameter(description = "Unique identifier of the subscription", required = true)
            @PathVariable Long subscriptionId,
            HttpServletRequest request, 
            HttpServletResponse response,
            @Parameter(description = "Query parameters for filtering")
            @RequestParam Map<String, String> params) throws IOException, FeedException {

        // Verify request contains valid query parameters
        if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/Subscription/{subscriptionId}/UsagePoint", params)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                             "Request contains invalid query parameter values!");
            return;
        }

        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        
        try {
            exportService.exportUsagePoints(subscriptionId,
                    response.getOutputStream(), new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Retrieves a specific UsagePoint within a subscription context.
     * 
     * @param subscriptionId Unique identifier for the subscription
     * @param usagePointId Unique identifier for the UsagePoint
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response for streaming ATOM XML content
     * @param params Query parameters for export filtering
     * @throws IOException if output stream cannot be written
     * @throws FeedException if ATOM entry generation fails
     */
    @GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @Operation(
        summary = "Get Subscription UsagePoint by ID",
        description = "Retrieves a specific UsagePoint resource within a subscription context. " +
                     "This provides access control based on the subscription's authorization scope."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved subscription UsagePoint",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
                             schema = @Schema(description = "ATOM entry containing subscription-scoped UsagePoint details"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid subscriptionId, usagePointId, or query parameters"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized access to this subscription or UsagePoint"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Subscription or UsagePoint not found"
        )
    })
    public void getSubscriptionUsagePoint(
            @Parameter(description = "Unique identifier of the subscription", required = true)
            @PathVariable Long subscriptionId,
            @Parameter(description = "Unique identifier of the UsagePoint", required = true)
            @PathVariable Long usagePointId,
            HttpServletRequest request, 
            HttpServletResponse response,
            @Parameter(description = "Query parameters for export filtering")
            @RequestParam Map<String, String> params) throws IOException, FeedException {

        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        
        try {
            exportService.exportUsagePoint(subscriptionId, usagePointId,
                    response.getOutputStream(), new ExportFilter(params));
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
     * @return Subscription ID if available, null otherwise
     */
    private Long getSubscriptionId(HttpServletRequest request) {
        // Implementation would extract subscription ID from OAuth2 context
        // or request attributes based on authorization flow
        return 1L; // Placeholder - implement based on security context
    }
}