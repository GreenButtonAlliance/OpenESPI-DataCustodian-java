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
import org.greenbuttonalliance.espi.common.domain.usage.ApplicationInformationEntity;
import org.greenbuttonalliance.espi.common.domain.usage.AuthorizationEntity;
import org.greenbuttonalliance.espi.common.models.atom.DateTimeType;
import org.greenbuttonalliance.espi.common.service.AuthorizationService;
import org.greenbuttonalliance.espi.common.service.ResourceService;
import org.greenbuttonalliance.espi.common.utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Service Status", description = "System Service Status Information API")
public class ServiceStatusRESTController {

	private final ResourceService resourceService;
	private final AuthorizationService authorizationService;

	@Autowired
	public ServiceStatusRESTController(
			ResourceService resourceService,
			AuthorizationService authorizationService) {
		this.resourceService = resourceService;
		this.authorizationService = authorizationService;
	}

	private String getApplicationStatus(HttpServletRequest request) {
		String token = request.getHeader("authorization");
		String applicationStatus = "0";

		if (token != null) {
			token = token.replace("Bearer ", "");
			AuthorizationEntity authorization = authorizationService
					.findByAccessToken(token);
			if (authorization != null) {
				ApplicationInformationEntity applicationinformation = authorization
						.getApplicationInformation();
				if (applicationinformation != null) {
					applicationStatus = applicationinformation
							.getDataCustodianApplicationStatus();
				}
			}
		}

		return applicationStatus;

	}

	// ROOT RESTful Forms
	//
	@GetMapping(value = "/ServiceStatus", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Get Service Status",
			description = "Returns the current service status information including application status and system health"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Service status retrieved successfully",
					content = @Content(
							mediaType = MediaType.APPLICATION_ATOM_XML_VALUE,
							schema = @Schema(description = "ESPI ServiceStatus XML")
					)
			),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "500", description = "Internal server error")
	})
	public void index(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		DateTimeType updated = DateConverter.toDateTimeType(new Date());
		String temp = updated.getValue().toXMLFormat();
		String uuid = UUID.randomUUID().toString();

		response.getOutputStream().println(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		response.getOutputStream()
				.println(
						"<ServiceStatus xmlns=\"http://naesb.org/espi\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\">");
		response.getOutputStream().println(
				"  <currentStatus>" + getApplicationStatus(request)
						+ "</currentStatus>\n</ServiceStatus>");

	}


}
