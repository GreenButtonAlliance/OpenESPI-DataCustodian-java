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
import org.greenbuttonalliance.espi.common.domain.customer.entity.CustomerAccountEntity;
import org.greenbuttonalliance.espi.common.dto.customer.CustomerAccountDto;
import org.greenbuttonalliance.espi.common.service.customer.CustomerAccountService;
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
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * RESTful controller for managing Customer Account resources according to the 
 * Green Button Alliance ESPI (Energy Services Provider Interface) specification.
 * 
 * CustomerAccount represents a billing account relationship between a customer and 
 * utility company, including account status, billing preferences, payment history,
 * and financial arrangements.
 * 
 * Features:
 * - Account lifecycle management (active, suspended, closed)
 * - Billing and payment tracking
 * - Service agreements and rate schedules
 * - Account hierarchy for commercial customers
 * - Financial reporting and statements
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Customer Account", description = "Customer Billing Account Management API")
@SecurityRequirement(name = "OAuth2")
public class CustomerAccountRESTController {

    private final CustomerAccountService customerAccountService;
    private final ExportService exportService;

    @Autowired
    public CustomerAccountRESTController(
            CustomerAccountService customerAccountService,
            ExportService exportService) {
        this.customerAccountService = customerAccountService;
        this.exportService = exportService;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleGenericException() {
        // Generic exception handler
    }

    // ================================
    // Customer Account Collection APIs
    // ================================

    /**
     * Retrieves all CustomerAccount resources.
     * 
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response for streaming ATOM XML content
     * @param params Query parameters for filtering and pagination
     * @throws IOException if output stream cannot be written
     * @throws FeedException if ATOM feed generation fails
     */
    @GetMapping(value = "/CustomerAccount", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ACCOUNT_READ')")
    @Operation(
        summary = "Get CustomerAccount Collection",
        description = "Retrieves all customer billing accounts with optional filtering and pagination. " +
                     "Returns an ATOM feed containing CustomerAccount entries including account status, " +
                     "billing information, and service relationships.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"ACCOUNT_READ"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved CustomerAccount collection",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
                             schema = @Schema(description = "ATOM feed containing CustomerAccount entries"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid query parameters provided"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized access to account resources"
        )
    })
    public void getCustomerAccountCollection(
            HttpServletRequest request, 
            HttpServletResponse response,
            @Parameter(description = "Query parameters for filtering (published-max, published-min, updated-max, updated-min, max-results, start-index)")
            @RequestParam Map<String, String> params) throws IOException, FeedException {

        // Verify request contains valid query parameters
        if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/CustomerAccount", params)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                             "Request contains invalid query parameter values!");
            return;
        }

        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        
        try {
            exportService.exportCustomerAccounts(response.getOutputStream(), 
                    new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Retrieves a specific CustomerAccount by ID.
     * 
     * @param customerAccountId Unique identifier for the CustomerAccount
     * @param request HTTP servlet request for authorization context
     * @param response HTTP response for streaming ATOM XML content
     * @param params Query parameters for export filtering
     * @throws IOException if output stream cannot be written
     * @throws FeedException if ATOM entry generation fails
     */
    @GetMapping(value = "/CustomerAccount/{customerAccountId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ACCOUNT_READ') and @accountSecurityService.hasAccessToAccount(authentication, #customerAccountId)")
    @Operation(
        summary = "Get CustomerAccount by ID",
        description = "Retrieves a specific customer billing account by its unique identifier. " +
                     "Returns an ATOM entry containing the CustomerAccount details including " +
                     "account status, billing cycle, payment methods, and service agreements.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"ACCOUNT_READ"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved CustomerAccount",
            content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
                             schema = @Schema(description = "ATOM entry containing CustomerAccount details"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid customerAccountId or query parameters"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized access to this account"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "CustomerAccount not found"
        )
    })
    public void getCustomerAccount(
            @Parameter(description = "Unique identifier of the CustomerAccount", required = true)
            @PathVariable UUID customerAccountId,
            HttpServletRequest request, 
            HttpServletResponse response,
            @Parameter(description = "Query parameters for export filtering")
            @RequestParam Map<String, String> params) throws IOException, FeedException {

        // Verify request contains valid query parameters
        if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/CustomerAccount/{customerAccountId}", params)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                             "Request contains invalid query parameter values!");
            return;
        }

        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        
        try {
            exportService.exportCustomerAccount(customerAccountId, response.getOutputStream(), 
                    new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Creates a new CustomerAccount resource.
     * 
     * @param customerAccountDto CustomerAccount data
     * @param request HTTP servlet request for authorization context
     * @return Created CustomerAccount response
     */
    @PostMapping(value = "/CustomerAccount", 
                consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE}, 
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ACCOUNT_WRITE') and hasRole('DATA_CUSTODIAN')")
    @Operation(
        summary = "Create CustomerAccount",
        description = "Creates a new customer billing account. Establishes the relationship " +
                     "between a customer and utility service including billing preferences, " +
                     "payment methods, and service level agreements.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"ACCOUNT_WRITE"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Successfully created CustomerAccount",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                             schema = @Schema(implementation = CustomerAccountDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid CustomerAccount data"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized to create accounts"
        )
    })
    public ResponseEntity<CustomerAccountDto> createCustomerAccount(
            @Parameter(description = "CustomerAccount data", required = true)
            @Valid @RequestBody CustomerAccountDto customerAccountDto,
            HttpServletRequest request) {

        try {
            CustomerAccountEntity customerAccount = customerAccountService.createCustomerAccount(customerAccountDto);
            CustomerAccountDto responseDto = customerAccountService.toDto(customerAccount);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates an existing CustomerAccount resource.
     * 
     * @param customerAccountId Unique identifier for the CustomerAccount to update
     * @param customerAccountDto Updated CustomerAccount data
     * @param request HTTP servlet request for authorization context
     * @return Updated CustomerAccount response
     */
    @PutMapping(value = "/CustomerAccount/{customerAccountId}", 
               consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE}, 
               produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ACCOUNT_WRITE') and @accountSecurityService.hasAccessToAccount(authentication, #customerAccountId)")
    @Operation(
        summary = "Update CustomerAccount",
        description = "Updates an existing customer billing account. Allows modification of " +
                     "account status, billing preferences, payment methods, and service configurations.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"ACCOUNT_WRITE"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully updated CustomerAccount",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                             schema = @Schema(implementation = CustomerAccountDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid CustomerAccount data"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized to update this account"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "CustomerAccount not found"
        )
    })
    public ResponseEntity<CustomerAccountDto> updateCustomerAccount(
            @Parameter(description = "Unique identifier of the CustomerAccount to update", required = true)
            @PathVariable UUID customerAccountId,
            @Parameter(description = "Updated CustomerAccount data", required = true)
            @Valid @RequestBody CustomerAccountDto customerAccountDto,
            HttpServletRequest request) {

        try {
            CustomerAccountEntity customerAccount = customerAccountService.updateCustomerAccount(customerAccountId, customerAccountDto);
            CustomerAccountDto responseDto = customerAccountService.toDto(customerAccount);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deletes a CustomerAccount resource.
     * 
     * @param customerAccountId Unique identifier for the CustomerAccount to delete
     * @param request HTTP servlet request for authorization context
     * @return Deletion confirmation response
     */
    @DeleteMapping("/CustomerAccount/{customerAccountId}")
    @PreAuthorize("hasAuthority('SCOPE_ACCOUNT_DELETE') and hasRole('DATA_CUSTODIAN')")
    @Operation(
        summary = "Delete CustomerAccount", 
        description = "Removes a customer billing account. This will close the account, " +
                     "finalize billing, and archive transaction history while maintaining " +
                     "regulatory compliance for record retention.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"ACCOUNT_DELETE"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Successfully deleted CustomerAccount"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized to delete accounts"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "CustomerAccount not found"
        )
    })
    public ResponseEntity<Void> deleteCustomerAccount(
            @Parameter(description = "Unique identifier of the CustomerAccount to delete", required = true)
            @PathVariable UUID customerAccountId,
            HttpServletRequest request) {

        try {
            customerAccountService.deleteCustomerAccount(customerAccountId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================================
    // Customer Account Financial Management APIs
    // ============================================

    /**
     * Retrieves the current account balance and billing status.
     * 
     * @param customerAccountId Unique identifier for the CustomerAccount
     * @param request HTTP servlet request for authorization context
     * @return Account balance and billing information
     */
    @GetMapping(value = "/CustomerAccount/{customerAccountId}/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ACCOUNT_READ') and @accountSecurityService.hasAccessToAccount(authentication, #customerAccountId)")
    @Operation(
        summary = "Get Account Balance",
        description = "Retrieves current account balance, billing status, payment due dates, " +
                     "and outstanding charges for the specified customer account.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"ACCOUNT_READ"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved account balance"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized access to account financial data"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "CustomerAccount not found"
        )
    })
    public ResponseEntity<Map<String, Object>> getAccountBalance(
            @Parameter(description = "Unique identifier of the CustomerAccount", required = true)
            @PathVariable UUID customerAccountId,
            HttpServletRequest request) {

        try {
            Map<String, Object> balanceInfo = customerAccountService.getAccountBalance(customerAccountId);
            return ResponseEntity.ok(balanceInfo);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates account billing preferences and payment methods.
     * 
     * @param customerAccountId Unique identifier for the CustomerAccount
     * @param billingPreferences Updated billing preferences
     * @param request HTTP servlet request for authorization context
     * @return Update confirmation response
     */
    @PutMapping(value = "/CustomerAccount/{customerAccountId}/billing", 
               consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ACCOUNT_WRITE') and @accountSecurityService.hasAccessToAccount(authentication, #customerAccountId)")
    @Operation(
        summary = "Update Billing Preferences",
        description = "Updates account billing preferences including billing cycle, " +
                     "payment methods, electronic billing options, and notification settings.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"ACCOUNT_WRITE"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully updated billing preferences"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid billing preference data"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized to modify billing preferences"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "CustomerAccount not found"
        )
    })
    public ResponseEntity<Void> updateBillingPreferences(
            @Parameter(description = "Unique identifier of the CustomerAccount", required = true)
            @PathVariable UUID customerAccountId,
            @Parameter(description = "Updated billing preferences", required = true)
            @RequestBody Map<String, Object> billingPreferences,
            HttpServletRequest request) {

        try {
            customerAccountService.updateBillingPreferences(customerAccountId, billingPreferences);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Suspends a customer account for non-payment or other issues.
     * 
     * @param customerAccountId Unique identifier for the CustomerAccount
     * @param suspensionReason Reason for account suspension
     * @param request HTTP servlet request for authorization context
     * @return Suspension confirmation response
     */
    @PostMapping(value = "/CustomerAccount/{customerAccountId}/suspend")
    @PreAuthorize("hasAuthority('SCOPE_ACCOUNT_ADMIN') and hasRole('DATA_CUSTODIAN')")
    @Operation(
        summary = "Suspend Customer Account",
        description = "Suspends a customer account due to non-payment, policy violations, " +
                     "or other administrative reasons. This affects service delivery and billing.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"ACCOUNT_ADMIN"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully suspended account"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized to suspend accounts"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "CustomerAccount not found"
        )
    })
    public ResponseEntity<Void> suspendAccount(
            @Parameter(description = "Unique identifier of the CustomerAccount", required = true)
            @PathVariable UUID customerAccountId,
            @Parameter(description = "Reason for suspension", required = true)
            @RequestParam String suspensionReason,
            HttpServletRequest request) {

        try {
            customerAccountService.suspendAccount(customerAccountId, suspensionReason);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reactivates a suspended customer account.
     * 
     * @param customerAccountId Unique identifier for the CustomerAccount
     * @param request HTTP servlet request for authorization context
     * @return Reactivation confirmation response
     */
    @PostMapping(value = "/CustomerAccount/{customerAccountId}/reactivate")
    @PreAuthorize("hasAuthority('SCOPE_ACCOUNT_ADMIN') and hasRole('DATA_CUSTODIAN')")
    @Operation(
        summary = "Reactivate Customer Account",
        description = "Reactivates a previously suspended customer account. " +
                     "Restores normal billing and service delivery operations.",
        security = {@SecurityRequirement(name = "OAuth2", scopes = {"ACCOUNT_ADMIN"})}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully reactivated account"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized to reactivate accounts"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "CustomerAccount not found"
        )
    })
    public ResponseEntity<Void> reactivateAccount(
            @Parameter(description = "Unique identifier of the CustomerAccount", required = true)
            @PathVariable UUID customerAccountId,
            HttpServletRequest request) {

        try {
            customerAccountService.reactivateAccount(customerAccountId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}