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
import org.greenbuttonalliance.espi.common.domain.usage.TimeConfigurationEntity;
import org.greenbuttonalliance.espi.common.service.*;
import org.greenbuttonalliance.espi.common.utils.ExportFilter;
import org.greenbuttonalliance.espi.datacustodian.utils.VerifyURLParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Time Configuration", description = "Time Configuration Management API")
public class TimeConfigurationRESTController {

	private final TimeConfigurationService timeConfigurationService;
	private final RetailCustomerService retailCustomerService;
	private final UsagePointService usagePointService;
	private final ExportService exportService;
	private final ResourceService resourceService;

	@Autowired
	public TimeConfigurationRESTController(
			TimeConfigurationService timeConfigurationService,
			RetailCustomerService retailCustomerService,
			UsagePointService usagePointService,
			ExportService exportService,
			ResourceService resourceService) {
		this.timeConfigurationService = timeConfigurationService;
		this.retailCustomerService = retailCustomerService;
		this.usagePointService = usagePointService;
		this.exportService = exportService;
		this.resourceService = resourceService;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
	}

	// ROOT RESTFul Forms
	//
	@GetMapping(value = "/TimeConfiguration", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Get Time Configurations",
			description = "Retrieves all time configuration resources for the system"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Time configurations retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access")
	})
	public void index(HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries("/espi/1_1/resource/TimeConfiguration", params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		exportService.exportTimeConfigurations(response.getOutputStream(),
				new ExportFilter(params));
	}

	@GetMapping(value = "/TimeConfiguration/{timeConfigurationId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Get Time Configuration",
			description = "Retrieves a specific time configuration resource by ID"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Time configuration retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
			@ApiResponse(responseCode = "404", description = "Time configuration not found"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access")
	})
	public void show(HttpServletResponse response,
			@Parameter(description = "Time configuration identifier", required = true)
			@PathVariable Long timeConfigurationId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries("/espi/1_1/resource/TimeConfiguration/{timeConfigurationId}", params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			exportService.exportTimeConfiguration(timeConfigurationId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@PostMapping(value = "/TimeConfiguration",
			consumes = MediaType.APPLICATION_ATOM_XML_VALUE,
			produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Create Time Configuration",
			description = "Creates a new time configuration resource from ATOM XML data"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Time configuration created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid ATOM XML data"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access")
	})
	public void create(HttpServletResponse response,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			TimeConfigurationEntity timeConfiguration = this.timeConfigurationService
					.importResource(stream);
			exportService.exportTimeConfiguration(timeConfiguration.getId(),
					response.getOutputStream(), new ExportFilter(
							new HashMap<String, String>()));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@PutMapping(value = "/TimeConfiguration/{timeConfigurationId}",
			consumes = MediaType.APPLICATION_ATOM_XML_VALUE,
			produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Update Time Configuration",
			description = "Updates an existing time configuration resource with new ATOM XML data"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Time configuration updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid ATOM XML data"),
			@ApiResponse(responseCode = "404", description = "Time configuration not found"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access")
	})
	public void update(HttpServletResponse response,
			@Parameter(description = "Time configuration identifier", required = true)
			@PathVariable Long timeConfigurationId,
			@RequestParam Map<String, String> params, InputStream stream) {

		try {
			timeConfigurationService.importResource(stream);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	@DeleteMapping(value = "/TimeConfiguration/{timeConfigurationId}")
	@Operation(
			summary = "Delete Time Configuration",
			description = "Deletes a specific time configuration resource"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Time configuration deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Time configuration not found"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "400", description = "Delete operation failed")
	})
	public void delete(HttpServletResponse response,
			@Parameter(description = "Time configuration identifier", required = true)
			@PathVariable Long timeConfigurationId,
			@RequestParam Map<String, String> params, InputStream stream) {
		try {
			resourceService.deleteById(timeConfigurationId,
					TimeConfigurationEntity.class);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}


}
