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
import org.greenbuttonalliance.espi.common.domain.usage.ApplicationInformationEntity;
import org.greenbuttonalliance.espi.common.service.ApplicationInformationService;
import org.greenbuttonalliance.espi.common.service.ExportService;
import org.greenbuttonalliance.espi.common.service.ResourceService;
import org.greenbuttonalliance.espi.common.utils.ExportFilter;
import org.greenbuttonalliance.espi.datacustodian.utils.VerifyURLParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * RESTful controller for managing ApplicationInformation resources according to the 
 * Green Button Alliance ESPI (Energy Services Provider Interface) specification.
 * 
 * Provides CRUD operations for OAuth2 application registration and management
 * in compliance with NAESB REQ.21 standards.
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Application Information", description = "OAuth2 Application Registration and Management API")
public class ApplicationInformationRESTController {

    private final ApplicationInformationService applicationInformationService;
    private final ExportService exportService;
    private final ResourceService resourceService;

    @Autowired
    public ApplicationInformationRESTController(
            ApplicationInformationService applicationInformationService,
            ExportService exportService,
            ResourceService resourceService) {
        this.applicationInformationService = applicationInformationService;
        this.exportService = exportService;
        this.resourceService = resourceService;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleGenericException() {
        // Generic exception handler
    }

    /**
     * Retrieves a collection of ApplicationInformation resources.
     * 
     * @param response HTTP response for streaming ATOM XML content
     * @param params Query parameters for filtering and pagination
     * @throws IOException if output stream cannot be written
     * @throws FeedException if ATOM feed generation fails
     */
    @GetMapping(value = "/ApplicationInformation", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @Operation(
        summary = "Get ApplicationInformation Collection",
        description = "Retrieves all registered OAuth2 applications with optional filtering and pagination. " +
                     "Returns an ATOM feed containing ApplicationInformation entries."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved ApplicationInformation collection",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
                             schema = @Schema(description = "ATOM feed containing ApplicationInformation entries"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid query parameters provided"
        )
    })
    public void getApplicationInformationCollection(
            HttpServletResponse response,
            @Parameter(description = "Query parameters for filtering (published-max, published-min, updated-max, updated-min, max-results, start-index)")
            @RequestParam Map<String, String> params) throws IOException, FeedException {

        // Verify request contains valid query parameters
        if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/ApplicationInformation", params)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                             "Request contains invalid query parameter values!");
            return;
        }

        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportApplicationInformations(response.getOutputStream(),
                new ExportFilter(params));
    }

    /**
     * Retrieves a specific ApplicationInformation resource by ID.
     * 
     * @param applicationInformationId Unique identifier for the ApplicationInformation
     * @param response HTTP response for streaming ATOM XML content  
     * @param params Query parameters for export filtering
     * @throws IOException if output stream cannot be written
     * @throws FeedException if ATOM entry generation fails
     */
    @GetMapping(value = "/ApplicationInformation/{applicationInformationId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @Operation(
        summary = "Get ApplicationInformation by ID",
        description = "Retrieves a specific OAuth2 application registration by its unique identifier. " +
                     "Returns an ATOM entry containing the ApplicationInformation details."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved ApplicationInformation",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
                             schema = @Schema(description = "ATOM entry containing ApplicationInformation details"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid applicationInformationId or query parameters"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "ApplicationInformation not found"
        )
    })
    public void getApplicationInformation(
            @Parameter(description = "Unique identifier of the ApplicationInformation", required = true)
            @PathVariable Long applicationInformationId,
            HttpServletResponse response,
            @Parameter(description = "Query parameters for export filtering")
            @RequestParam Map<String, String> params) throws IOException, FeedException {

        // Verify request contains valid query parameters
        if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/ApplicationInformation/{applicationInformationId}", params)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                             "Request contains invalid query parameter values!");
            return;
        }

        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        try {
            exportService.exportApplicationInformation(
                    applicationInformationId, response.getOutputStream(),
                    new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Creates a new ApplicationInformation resource.
     * 
     * @param response HTTP response for returning created resource
     * @param params Query parameters for export filtering
     * @param stream Input stream containing ATOM XML data
     * @throws IOException if input/output stream operations fail
     */
    @PostMapping(value = "/ApplicationInformation", 
                consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
                produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @Operation(
        summary = "Create ApplicationInformation",
        description = "Creates a new OAuth2 application registration. The request body should contain " +
                     "an ATOM entry with ApplicationInformation details including client credentials, " +
                     "redirect URIs, and scopes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Successfully created ApplicationInformation",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
                             schema = @Schema(description = "ATOM entry containing the created ApplicationInformation"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid ATOM XML format or ApplicationInformation data"
        )
    })
    public void createApplicationInformation(
            HttpServletResponse response,
            @Parameter(description = "Query parameters for export filtering")
            @RequestParam Map<String, String> params, 
            @Parameter(description = "ATOM XML containing ApplicationInformation data", required = true)
            @RequestBody InputStream stream) throws IOException {

        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        try {
            ApplicationInformationEntity applicationInformation = this.applicationInformationService
                    .importResource(stream);
            exportService.exportApplicationInformation(
                    applicationInformation.getId(), response.getOutputStream(),
                    new ExportFilter(params));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Updates an existing ApplicationInformation resource.
     * 
     * @param applicationInformationId Unique identifier for the ApplicationInformation to update
     * @param response HTTP response for returning updated resource
     * @param params Query parameters for export filtering  
     * @param stream Input stream containing updated ATOM XML data
     * @throws IOException if input/output stream operations fail
     */
    @PutMapping(value = "/ApplicationInformation/{applicationInformationId}", 
               consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
               produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @Operation(
        summary = "Update ApplicationInformation",
        description = "Updates an existing OAuth2 application registration. The request body should contain " +
                     "an ATOM entry with updated ApplicationInformation details."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully updated ApplicationInformation",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
                             schema = @Schema(description = "ATOM entry containing the updated ApplicationInformation"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid ATOM XML format or ApplicationInformation data"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "ApplicationInformation not found"
        )
    })
    public void updateApplicationInformation(
            @Parameter(description = "Unique identifier of the ApplicationInformation to update", required = true)
            @PathVariable Long applicationInformationId,
            HttpServletResponse response,
            @Parameter(description = "Query parameters for export filtering")
            @RequestParam Map<String, String> params,
            @Parameter(description = "ATOM XML containing updated ApplicationInformation data", required = true)
            @RequestBody InputStream stream) throws IOException {

        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        try {
            ApplicationInformationEntity applicationInformation = applicationInformationService
                    .importResource(stream);
            applicationInformation.setId(applicationInformationId);
            applicationInformationService.save(applicationInformation);
            
            exportService.exportApplicationInformation(
                    applicationInformationId, response.getOutputStream(),
                    new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Deletes an ApplicationInformation resource.
     * 
     * @param applicationInformationId Unique identifier for the ApplicationInformation to delete
     * @param response HTTP response
     */
    @DeleteMapping("/ApplicationInformation/{applicationInformationId}")
    @Operation(
        summary = "Delete ApplicationInformation", 
        description = "Removes an OAuth2 application registration. This will revoke all associated " +
                     "access tokens and authorizations."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully deleted ApplicationInformation"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "ApplicationInformation not found"
        )
    })
    public void deleteApplicationInformation(
            @Parameter(description = "Unique identifier of the ApplicationInformation to delete", required = true)
            @PathVariable Long applicationInformationId,
            HttpServletResponse response) {
        
        try {
            applicationInformationService.deleteById(applicationInformationId);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}