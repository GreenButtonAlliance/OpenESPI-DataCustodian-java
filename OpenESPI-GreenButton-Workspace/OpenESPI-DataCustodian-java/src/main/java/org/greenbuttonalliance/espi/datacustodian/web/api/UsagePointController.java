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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.greenbuttonalliance.espi.common.dto.usage.UsagePointDto;
import org.greenbuttonalliance.espi.common.service.UsagePointService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for ESPI Usage Point resources.
 * 
 * This controller implements the NAESB ESPI 1.0 REST API for Usage Points,
 * replacing the legacy Spring MVC controller with modern Spring Boot 3.5 patterns.
 * 
 * Supported endpoints:
 * - GET /espi/1_1/resource/UsagePoint - List all usage points
 * - GET /espi/1_1/resource/UsagePoint/{usagePointId} - Get specific usage point
 * - GET /espi/1_1/resource/Subscription/{subscriptionId}/UsagePoint - List subscription usage points
 * - GET /espi/1_1/resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId} - Get subscription usage point
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Usage Points", description = "ESPI Usage Point resource endpoints")
@SecurityRequirement(name = "oauth2")
public class UsagePointController {

    private final UsagePointService usagePointService;

    public UsagePointController(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }

    /**
     * Get all Usage Points (root collection).
     * Requires DataCustodian admin access or appropriate read scope.
     */
    @GetMapping(value = "/UsagePoint", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(
        summary = "Get all Usage Points",
        description = "Retrieve all Usage Points accessible to the authenticated client",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usage Points retrieved successfully",
                content = @Content(schema = @Schema(implementation = UsagePointDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient scope")
        }
    )
    @PreAuthorize("hasAuthority('SCOPE_DataCustodian_Admin_Access') or " +
                 "hasAuthority('SCOPE_FB_15_READ_3rd_party') or " +
                 "hasAuthority('SCOPE_FB_16_READ_3rd_party') or " +
                 "hasAuthority('SCOPE_FB_36_READ_3rd_party')")
    public ResponseEntity<List<UsagePointDto>> getAllUsagePoints(
            @Parameter(description = "Maximum number of results to return", example = "50")
            @RequestParam(defaultValue = "50") int limit,
            @Parameter(description = "Offset for pagination", example = "0")
            @RequestParam(defaultValue = "0") int offset,
            Authentication authentication) {
        
        List<UsagePointDto> usagePoints = usagePointService.findAll(limit, offset);
        return ResponseEntity.ok(usagePoints);
    }

    /**
     * Get specific Usage Point by ID (root resource).
     */
    @GetMapping(value = "/UsagePoint/{usagePointId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(
        summary = "Get Usage Point by ID",
        description = "Retrieve a specific Usage Point by its unique identifier",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usage Point retrieved successfully",
                content = @Content(schema = @Schema(implementation = UsagePointDto.class))),
            @ApiResponse(responseCode = "404", description = "Usage Point not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient scope")
        }
    )
    @PreAuthorize("hasAuthority('SCOPE_DataCustodian_Admin_Access') or " +
                 "hasAuthority('SCOPE_FB_15_READ_3rd_party') or " +
                 "hasAuthority('SCOPE_FB_16_READ_3rd_party') or " +
                 "hasAuthority('SCOPE_FB_36_READ_3rd_party')")
    public ResponseEntity<UsagePointDto> getUsagePoint(
            @Parameter(description = "Unique identifier of the Usage Point", required = true)
            @PathVariable UUID usagePointId,
            Authentication authentication) {
        
        return usagePointService.findById(usagePointId)
            .map(usagePoint -> ResponseEntity.ok(usagePoint))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get Usage Points for a specific Subscription.
     */
    @GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(
        summary = "Get Usage Points for Subscription",
        description = "Retrieve all Usage Points associated with a specific subscription",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usage Points retrieved successfully",
                content = @Content(schema = @Schema(implementation = UsagePointDto.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient scope")
        }
    )
    @PreAuthorize("hasAuthority('SCOPE_FB_15_READ_3rd_party') or " +
                 "hasAuthority('SCOPE_FB_16_READ_3rd_party') or " +
                 "hasAuthority('SCOPE_FB_36_READ_3rd_party')")
    public ResponseEntity<List<UsagePointDto>> getSubscriptionUsagePoints(
            @Parameter(description = "Unique identifier of the Subscription", required = true)
            @PathVariable UUID subscriptionId,
            @Parameter(description = "Maximum number of results to return", example = "50")
            @RequestParam(defaultValue = "50") int limit,
            @Parameter(description = "Offset for pagination", example = "0")
            @RequestParam(defaultValue = "0") int offset,
            Authentication authentication) {
        
        List<UsagePointDto> usagePoints = usagePointService.findBySubscriptionId(subscriptionId, limit, offset);
        return ResponseEntity.ok(usagePoints);
    }

    /**
     * Get specific Usage Point for a Subscription.
     */
    @GetMapping(value = "/Subscription/{subscriptionId}/UsagePoint/{usagePointId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(
        summary = "Get Usage Point for Subscription",
        description = "Retrieve a specific Usage Point associated with a subscription",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usage Point retrieved successfully",
                content = @Content(schema = @Schema(implementation = UsagePointDto.class))),
            @ApiResponse(responseCode = "404", description = "Usage Point or Subscription not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient scope")
        }
    )
    @PreAuthorize("hasAuthority('SCOPE_FB_15_READ_3rd_party') or " +
                 "hasAuthority('SCOPE_FB_16_READ_3rd_party') or " +
                 "hasAuthority('SCOPE_FB_36_READ_3rd_party')")
    public ResponseEntity<UsagePointDto> getSubscriptionUsagePoint(
            @Parameter(description = "Unique identifier of the Subscription", required = true)
            @PathVariable UUID subscriptionId,
            @Parameter(description = "Unique identifier of the Usage Point", required = true)
            @PathVariable UUID usagePointId,
            Authentication authentication) {
        
        return usagePointService.findBySubscriptionIdAndUsagePointId(subscriptionId, usagePointId)
            .map(usagePoint -> ResponseEntity.ok(usagePoint))
            .orElse(ResponseEntity.notFound().build());
    }
}