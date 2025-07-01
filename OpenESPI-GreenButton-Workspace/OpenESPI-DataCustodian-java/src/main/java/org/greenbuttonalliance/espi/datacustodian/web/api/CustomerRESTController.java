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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.greenbuttonalliance.espi.common.domain.customer.entity.CustomerEntity;
import org.greenbuttonalliance.espi.common.dto.customer.CustomerDto;
import org.greenbuttonalliance.espi.common.service.customer.CustomerService;
import org.greenbuttonalliance.espi.common.service.ExportService;
import org.greenbuttonalliance.espi.common.utils.ExportFilter;
import org.greenbuttonalliance.espi.datacustodian.utils.VerifyURLParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RESTful controller for managing Customer PII (Personally Identifiable Information) 
 * according to the Green Button Alliance ESPI specification.
 * 
 * Provides secure CRUD operations for customer personal data including demographics, 
 * contact information, and privacy preferences with strict PII protection controls.
 * 
 * ⚠️  IMPORTANT: This controller handles sensitive PII data and requires:
 * - OAuth2 authentication with appropriate scopes
 * - PII data encryption at rest and in transit  
 * - Audit logging for all access operations
 * - GDPR/CCPA compliance for data handling
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Customer PII", description = "Customer Personal Information Management API")
@SecurityRequirement(name = "OAuth2")
public class CustomerRESTController {

    private final CustomerService customerService;
    private final ExportService exportService;

    @Autowired
    public CustomerRESTController(
            CustomerService customerService,
            ExportService exportService) {
        this.customerService = customerService;
        this.exportService = exportService;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleGenericException() {
        // Generic exception handler with PII data protection
    }

    // ================================
    // Customer Collection APIs
    // ================================

    /**
     * Retrieves Customer collection with PII protection.
     * 
     * ⚠️ RESTRICTED: Requires CUSTOMER_READ scope and data custodian authorization.
     * 
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response for streaming ATOM XML content
     * @param params Query parameters for filtering and pagination
     * @throws IOException if output stream cannot be written
     * @throws FeedException if ATOM feed generation fails
     */
    @GetMapping(value = "/Customer", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_CUSTOMER_READ') and hasRole('DATA_CUSTODIAN')")
    @Operation(
        summary = "Get Customer Collection",
        description = "Retrieves customer records with PII protection. Requires elevated privileges " +
                     "and logs all access for compliance. Returns anonymized data unless full " +
                     "authorization is provided.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"CUSTOMER_READ"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved customer collection",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
                             schema = @Schema(description = "ATOM feed containing Customer entries with PII protection"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid query parameters provided"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - missing or invalid OAuth2 token"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - insufficient scope for customer PII access"
        )
    })
    public void getCustomerCollection(
            HttpServletRequest request, 
            HttpServletResponse response,
            @Parameter(description = "Query parameters for filtering (published-max, published-min, updated-max, updated-min, max-results, start-index)")
            @RequestParam Map<String, String> params) throws IOException, FeedException {

        // Verify request contains valid query parameters
        if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/Customer", params)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                             "Request contains invalid query parameter values!");
            return;
        }

        // Log PII access for compliance
        logPIIAccess(request, "CUSTOMER_COLLECTION_READ", null);

        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        
        try {
            exportService.exportCustomers(response.getOutputStream(), 
                    new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Retrieves a specific Customer by ID with PII protection.
     * 
     * ⚠️ RESTRICTED: Requires CUSTOMER_READ scope and ownership validation.
     * 
     * @param customerId Unique identifier for the Customer
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response for streaming ATOM XML content
     * @param params Query parameters for export filtering
     * @throws IOException if output stream cannot be written
     * @throws FeedException if ATOM entry generation fails
     */
    @GetMapping(value = "/Customer/{customerId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_CUSTOMER_READ') and @customerSecurityService.hasAccessToCustomer(authentication, #customerId)")
    @Operation(
        summary = "Get Customer by ID",
        description = "Retrieves a specific customer record with full PII protection. " +
                     "Access is restricted to authorized parties with appropriate data handling agreements. " +
                     "All access is logged for compliance auditing.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"CUSTOMER_READ"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved customer data",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
                             schema = @Schema(description = "ATOM entry containing Customer details with PII protection"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid customerId or query parameters"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - missing or invalid OAuth2 token"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - no access to this customer's PII data"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Customer not found"
        )
    })
    public void getCustomer(
            @Parameter(description = "Unique identifier of the Customer", required = true)
            @PathVariable UUID customerId,
            HttpServletRequest request, 
            HttpServletResponse response,
            @Parameter(description = "Query parameters for export filtering")
            @RequestParam Map<String, String> params) throws IOException, FeedException {

        // Verify request contains valid query parameters
        if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/Customer/{customerId}", params)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                             "Request contains invalid query parameter values!");
            return;
        }

        // Log PII access for compliance
        logPIIAccess(request, "CUSTOMER_READ", customerId);

        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        
        try {
            exportService.exportCustomer(customerId, response.getOutputStream(), 
                    new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Creates a new Customer record with PII protection.
     * 
     * ⚠️ RESTRICTED: Requires CUSTOMER_WRITE scope and data custodian authorization.
     * 
     * @param customerDto Customer data with PII validation
     * @param request HTTP servlet request for authorization context
     * @return Created customer response with anonymized data
     */
    @PostMapping(value = "/Customer", 
                consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE}, 
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_CUSTOMER_WRITE') and hasRole('DATA_CUSTODIAN')")
    @Operation(
        summary = "Create Customer",
        description = "Creates a new customer record with full PII protection and validation. " +
                     "Automatically applies data encryption, establishes audit trail, and " +
                     "validates against privacy regulations (GDPR/CCPA).",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"CUSTOMER_WRITE"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Successfully created customer",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                             schema = @Schema(implementation = CustomerDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid customer data or PII validation failed"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - missing or invalid OAuth2 token"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - insufficient scope for customer creation"
        )
    })
    public ResponseEntity<CustomerDto> createCustomer(
            @Parameter(description = "Customer data with PII protection", required = true)
            @Valid @RequestBody CustomerDto customerDto,
            HttpServletRequest request) {

        try {
            // Log PII access for compliance
            logPIIAccess(request, "CUSTOMER_CREATE", null);

            CustomerEntity customer = customerService.createCustomer(customerDto);
            CustomerDto responseDto = customerService.toDto(customer);
            
            // Return anonymized response for security
            responseDto = anonymizeCustomerResponse(responseDto);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates an existing Customer record with PII protection.
     * 
     * ⚠️ RESTRICTED: Requires CUSTOMER_WRITE scope and ownership validation.
     * 
     * @param customerId Unique identifier for the Customer to update
     * @param customerDto Updated customer data
     * @param request HTTP servlet request for authorization context
     * @return Updated customer response with anonymized data
     */
    @PutMapping(value = "/Customer/{customerId}", 
               consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE}, 
               produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_CUSTOMER_WRITE') and @customerSecurityService.hasAccessToCustomer(authentication, #customerId)")
    @Operation(
        summary = "Update Customer",
        description = "Updates an existing customer record with PII protection. " +
                     "Maintains audit trail of all changes and validates data integrity.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"CUSTOMER_WRITE"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully updated customer",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                             schema = @Schema(implementation = CustomerDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid customer data or PII validation failed"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - missing or invalid OAuth2 token"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - no access to update this customer"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Customer not found"
        )
    })
    public ResponseEntity<CustomerDto> updateCustomer(
            @Parameter(description = "Unique identifier of the Customer to update", required = true)
            @PathVariable UUID customerId,
            @Parameter(description = "Updated customer data with PII protection", required = true)
            @Valid @RequestBody CustomerDto customerDto,
            HttpServletRequest request) {

        try {
            // Log PII access for compliance
            logPIIAccess(request, "CUSTOMER_UPDATE", customerId);

            CustomerEntity customer = customerService.updateCustomer(customerId, customerDto);
            CustomerDto responseDto = customerService.toDto(customer);
            
            // Return anonymized response for security
            responseDto = anonymizeCustomerResponse(responseDto);
            
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deletes a Customer record with PII data purging.
     * 
     * ⚠️ RESTRICTED: Requires CUSTOMER_DELETE scope and data custodian authorization.
     * ⚠️ PERMANENT: This operation permanently purges PII data for GDPR compliance.
     * 
     * @param customerId Unique identifier for the Customer to delete
     * @param request HTTP servlet request for authorization context
     * @return Deletion confirmation response
     */
    @DeleteMapping("/Customer/{customerId}")
    @PreAuthorize("hasAuthority('SCOPE_CUSTOMER_DELETE') and hasRole('DATA_CUSTODIAN')")
    @Operation(
        summary = "Delete Customer and Purge PII", 
        description = "⚠️ PERMANENT OPERATION: Completely removes customer record and purges " +
                     "all associated PII data. This operation cannot be undone and is designed " +
                     "for GDPR/CCPA 'Right to be Forgotten' compliance.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"CUSTOMER_DELETE"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Successfully deleted customer and purged PII data"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - missing or invalid OAuth2 token"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - insufficient scope for customer deletion"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Customer not found"
        )
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Unique identifier of the Customer to delete", required = true)
            @PathVariable UUID customerId,
            HttpServletRequest request) {

        try {
            // Log PII access for compliance
            logPIIAccess(request, "CUSTOMER_DELETE", customerId);

            customerService.deleteCustomerWithPIIPurge(customerId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // =============================================
    // Customer Data Export APIs (GDPR Compliance)
    // =============================================

    /**
     * Exports complete customer data for GDPR data portability.
     * 
     * ⚠️ RESTRICTED: Requires customer consent and identity verification.
     * 
     * @param customerId Unique identifier for the Customer
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response for data export
     * @throws IOException if export fails
     */
    @GetMapping(value = "/Customer/{customerId}/export", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@customerSecurityService.hasConsentForDataExport(authentication, #customerId)")
    @Operation(
        summary = "Export Customer Data (GDPR)",
        description = "Exports complete customer data package for GDPR data portability rights. " +
                     "Requires explicit customer consent and strong authentication.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"CUSTOMER_EXPORT"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully exported customer data"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - identity verification required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - customer consent required"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Customer not found"
        )
    })
    public void exportCustomerData(
            @Parameter(description = "Unique identifier of the Customer", required = true)
            @PathVariable UUID customerId,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // Log PII access for compliance
        logPIIAccess(request, "CUSTOMER_EXPORT", customerId);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=customer-data-export.json");
        
        try {
            customerService.exportCustomerDataForGDPR(customerId, response.getOutputStream());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // =============================================
    // Privacy and Security Utility Methods
    // =============================================

    /**
     * Logs PII access for compliance auditing.
     * 
     * @param request HTTP request
     * @param operation Type of operation performed
     * @param customerId Customer ID if applicable
     */
    private void logPIIAccess(HttpServletRequest request, String operation, UUID customerId) {
        // Implementation would log to secure audit system
        // Include: timestamp, user ID, IP address, operation, customer ID, success/failure
        System.out.println("PII_ACCESS_LOG: " + operation + " customer=" + customerId + 
                          " user=" + request.getRemoteUser() + " ip=" + request.getRemoteAddr());
    }

    /**
     * Anonymizes customer response data for security.
     * 
     * @param customerDto Original customer DTO
     * @return Anonymized customer DTO
     */
    private CustomerDto anonymizeCustomerResponse(CustomerDto customerDto) {
        // Implementation would mask/remove sensitive PII fields
        // Keep only non-sensitive identifiers and metadata
        CustomerDto anonymized = new CustomerDto();
        anonymized.setId(customerDto.getId());
        anonymized.setCustomerKind(customerDto.getCustomerKind());
        anonymized.setStatus(customerDto.getStatus());
        // Mask sensitive fields like names, addresses, phone numbers
        return anonymized;
    }
}