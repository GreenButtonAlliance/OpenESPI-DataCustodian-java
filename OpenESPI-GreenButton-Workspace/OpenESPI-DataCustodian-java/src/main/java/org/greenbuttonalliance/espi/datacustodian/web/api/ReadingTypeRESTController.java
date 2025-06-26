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
import org.greenbuttonalliance.espi.common.domain.usage.ReadingTypeEntity;
import org.greenbuttonalliance.espi.common.service.ExportService;
import org.greenbuttonalliance.espi.common.service.ReadingTypeService;
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
 * RESTful controller for managing ReadingType resources according to the 
 * Green Button Alliance ESPI (Energy Services Provider Interface) specification.
 * 
 * ReadingType represents the type of reading being measured (e.g., energy consumed, 
 * energy produced, voltage, current) and includes metadata about units of measure, 
 * measurement kind, phase, and accumulation behavior.
 */
@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Reading Type", description = "Smart Meter Reading Type Metadata Management API")
public class ReadingTypeRESTController {

	private final ReadingTypeService readingTypeService;
	private final ResourceService resourceService;
	private final ExportService exportService;

	@Autowired
	public ReadingTypeRESTController(
			ReadingTypeService readingTypeService,
			ResourceService resourceService,
			ExportService exportService) {
		this.readingTypeService = readingTypeService;
		this.resourceService = resourceService;
		this.exportService = exportService;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
		// Generic exception handler
	}

	// ================================
	// ReadingType Collection APIs
	// ================================

	/**
	 * Retrieves all ReadingType resources.
	 * 
	 * @param response HTTP response for streaming ATOM XML content
	 * @param params Query parameters for filtering and pagination
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM feed generation fails
	 */
	@GetMapping(value = "/ReadingType", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get ReadingType Collection",
		description = "Retrieves all ReadingType resources with optional filtering and pagination. " +
					 "Returns an ATOM feed containing ReadingType entries that define measurement " +
					 "metadata such as units, phases, and accumulation behavior."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved ReadingType collection",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE, 
							 schema = @Schema(description = "ATOM feed containing ReadingType entries"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid query parameters provided"
		)
	})
	public void getReadingTypeCollection(
			HttpServletResponse response,
			@Parameter(description = "Query parameters for filtering (published-max, published-min, updated-max, updated-min, max-results, start-index)")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/ReadingType", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportReadingTypes(response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Retrieves a specific ReadingType resource by ID.
	 * 
	 * @param response HTTP response for streaming ATOM XML content
	 * @param readingTypeId Unique identifier for the ReadingType
	 * @param params Query parameters for export filtering
	 * @throws IOException if output stream cannot be written
	 * @throws FeedException if ATOM entry generation fails
	 */
	@GetMapping(value = "/ReadingType/{readingTypeId}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Get ReadingType by ID",
		description = "Retrieves a specific ReadingType resource by its unique identifier. " +
					 "Returns an ATOM entry containing the ReadingType details including " +
					 "measurement kind, unit of measure, phase information, and accumulation behavior."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved ReadingType",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing ReadingType details"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid readingTypeId or query parameters"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "ReadingType not found"
		)
	})
	public void getReadingType(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the ReadingType", required = true)
			@PathVariable Long readingTypeId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params) throws IOException, FeedException {

		// Verify request contains valid query parameters
		if (!VerifyURLParams.verifyEntries("/espi/1_1/resource/ReadingType/{readingTypeId}", params)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
							 "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			exportService.exportReadingType(readingTypeId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Creates a new ReadingType resource.
	 * 
	 * @param response HTTP response for returning created resource
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 */
	@PostMapping(value = "/ReadingType", 
				consumes = MediaType.APPLICATION_ATOM_XML_VALUE, 
				produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Create ReadingType",
		description = "Creates a new ReadingType resource representing measurement metadata. " +
					 "The request body should contain an ATOM entry with ReadingType details including " +
					 "measurement kind, unit of measure, multiplier, phase, and accumulation behavior."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201", 
			description = "Successfully created ReadingType",
			content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							 schema = @Schema(description = "ATOM entry containing the created ReadingType"))
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or ReadingType data"
		)
	})
	public void createReadingType(
			HttpServletResponse response,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing ReadingType data", required = true)
			@RequestBody InputStream stream) throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		
		try {
			ReadingTypeEntity readingType = this.readingTypeService.importResource(stream);
			exportService.exportReadingType(readingType.getId(),
					response.getOutputStream(), new ExportFilter(params));
			response.setStatus(HttpServletResponse.SC_CREATED);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	/**
	 * Updates an existing ReadingType resource.
	 * 
	 * @param response HTTP response for returning updated resource
	 * @param readingTypeId Unique identifier for the ReadingType to update
	 * @param params Query parameters for export filtering
	 * @param stream Input stream containing updated ATOM XML data
	 * @throws IOException if input/output stream operations fail
	 */
	@PutMapping(value = "/ReadingType/{readingTypeId}", 
			   consumes = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
		summary = "Update ReadingType",
		description = "Updates an existing ReadingType resource. The request body should contain " +
					 "an ATOM entry with updated ReadingType details."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully updated ReadingType"
		),
		@ApiResponse(
			responseCode = "400", 
			description = "Invalid ATOM XML format or ReadingType data"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "ReadingType not found"
		)
	})
	public void updateReadingType(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the ReadingType to update", required = true)
			@PathVariable Long readingTypeId,
			@Parameter(description = "Query parameters for export filtering")
			@RequestParam Map<String, String> params, 
			@Parameter(description = "ATOM XML containing updated ReadingType data", required = true)
			@RequestBody InputStream stream) throws IOException {

		ReadingTypeEntity existingReadingType = readingTypeService.findById(readingTypeId);

		if (existingReadingType != null) {
			try {
				readingTypeService.importResource(stream);
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * Deletes a ReadingType resource.
	 * 
	 * @param response HTTP response
	 * @param readingTypeId Unique identifier for the ReadingType to delete
	 */
	@DeleteMapping("/ReadingType/{readingTypeId}")
	@Operation(
		summary = "Delete ReadingType", 
		description = "Removes a ReadingType resource. This operation should be used carefully " +
					 "as it may affect associated meter readings and usage data."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully deleted ReadingType"
		),
		@ApiResponse(
			responseCode = "404", 
			description = "ReadingType not found"
		),
		@ApiResponse(
			responseCode = "409", 
			description = "ReadingType cannot be deleted due to existing references"
		)
	})
	public void deleteReadingType(
			HttpServletResponse response,
			@Parameter(description = "Unique identifier of the ReadingType to delete", required = true)
			@PathVariable Long readingTypeId) {

		try {
			resourceService.deleteById(readingTypeId, ReadingType.class);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}


}
