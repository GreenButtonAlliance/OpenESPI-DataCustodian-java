/*
 *     Copyright (c) 2018-2019 Green Button Alliance, Inc.
 *
 *     Portions copyright (c) 2013-2018 EnergyOS.org
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
 */


package org.greenbuttonalliance.espi.datacustodian.web.api;

import com.sun.syndication.io.FeedException;
import org.greenbuttonalliance.espi.common.domain.Routes;
import org.greenbuttonalliance.espi.common.domain.TimeConfiguration;
import org.greenbuttonalliance.espi.common.service.*;
import org.greenbuttonalliance.espi.common.utils.ExportFilter;
import org.greenbuttonalliance.espi.datacustodian.utils.VerifyURLParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TimeConfigurationRESTController {

	@Autowired
	private TimeConfigurationService timeConfigurationService;

	@Autowired
	private RetailCustomerService retailCustomerService;

	@Autowired
	private UsagePointService usagePointService;

	@Autowired
	private ExportService exportService;

	@Autowired
	private ResourceService resourceService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
	}

	// ROOT RESTFul Forms
	//
	@RequestMapping(value = Routes.ROOT_TIME_CONFIGURATION_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.ROOT_TIME_CONFIGURATION_COLLECTION, params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		exportService.exportTimeConfigurations(response.getOutputStream(),
				new ExportFilter(params));
	}

	@RequestMapping(value = Routes.ROOT_TIME_CONFIGURATION_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long timeConfigurationId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.ROOT_TIME_CONFIGURATION_MEMBER, params)) {

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

	@RequestMapping(value = Routes.ROOT_TIME_CONFIGURATION_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			TimeConfiguration timeConfiguration = this.timeConfigurationService
					.importResource(stream);
			exportService.exportTimeConfiguration(timeConfiguration.getId(),
					response.getOutputStream(), new ExportFilter(
							new HashMap<String, String>()));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.ROOT_TIME_CONFIGURATION_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long timeConfigurationId,
			@RequestParam Map<String, String> params, InputStream stream) {

		try {
			timeConfigurationService.importResource(stream);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	@RequestMapping(value = Routes.ROOT_TIME_CONFIGURATION_MEMBER, method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response,
			@PathVariable Long timeConfigurationId,
			@RequestParam Map<String, String> params, InputStream stream) {
		try {
			resourceService.deleteById(timeConfigurationId,
					TimeConfiguration.class);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	public void setTimeConfigurationService(
			TimeConfigurationService timeConfigurationService) {
		this.timeConfigurationService = timeConfigurationService;
	}

	public TimeConfigurationService getTimeConfigurationService() {
		return this.timeConfigurationService;
	}

	public void setRetailCustomerService(
			RetailCustomerService retailCustomerService) {
		this.retailCustomerService = retailCustomerService;
	}

	public RetailCustomerService getRetailCustomerService() {
		return this.retailCustomerService;
	}

	public void setUsagePointService(UsagePointService usagePointService) {
		this.usagePointService = usagePointService;
	}

	public UsagePointService getUsagePointService() {
		return this.usagePointService;
	}

	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	public ExportService getExportService() {
		return this.exportService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public ResourceService getResourceService() {
		return this.resourceService;
	}

}
