/*
 * Copyright 2013, 2014 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.energyos.espi.datacustodian.web.api;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.TimeConfiguration;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.TimeConfigurationService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.utils.ExportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sun.syndication.io.FeedException;

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

		exportService.exportTimeConfigurations(response.getOutputStream(),
				new ExportFilter(params));
	}

	@RequestMapping(value = Routes.ROOT_TIME_CONFIGURATION_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long timeConfigurationId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

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

	// XPath RESTful Forms
	//
	@RequestMapping(value = Routes.TIME_CONFIGURATION_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		exportService.exportTimeConfigurations(retailCustomerId, usagePointId,
				response.getOutputStream(), new ExportFilter(params));
	}

	@RequestMapping(value = Routes.TIME_CONFIGURATION_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long usagePointId,
			@PathVariable Long timeConfigurationId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		try {
			exportService.exportTimeConfiguration(retailCustomerId,
					usagePointId, timeConfigurationId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.TIME_CONFIGURATION_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		if (null != resourceService.findIdByXPath(retailCustomerId,
				usagePointId, UsagePoint.class)) {

			try {
				UsagePoint usagePoint = usagePointService
						.findById(usagePointId);

				TimeConfiguration timeConfiguration = this.timeConfigurationService
						.importResource(stream);
				timeConfigurationService.associateByUUID(usagePoint,
						timeConfiguration.getUUID());
				exportService.exportTimeConfiguration(retailCustomerId,
						usagePointId, timeConfiguration.getId(),
						response.getOutputStream(), new ExportFilter(params));
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.TIME_CONFIGURATION_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long usagePointId,
			@PathVariable Long timeConfigurationId,
			@RequestParam Map<String, String> params, InputStream stream) {

		TimeConfiguration existingTimeConfiguration;

		existingTimeConfiguration = timeConfigurationService
				.findById(timeConfigurationId);

		if (existingTimeConfiguration != null) {
			try {
				TimeConfiguration timeConfiguration = timeConfigurationService
						.importResource(stream);
				existingTimeConfiguration.merge(timeConfiguration);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	@RequestMapping(value = Routes.TIME_CONFIGURATION_MEMBER, method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long usagePointId,
			@PathVariable Long timeConfigurationId,
			@RequestParam Map<String, String> params, InputStream stream) {

		this.timeConfigurationService.deleteById(timeConfigurationId);
	}

	public void setRetailCustomerService(
			RetailCustomerService retailCustomerService) {
		this.retailCustomerService = retailCustomerService;
	}

	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
}
