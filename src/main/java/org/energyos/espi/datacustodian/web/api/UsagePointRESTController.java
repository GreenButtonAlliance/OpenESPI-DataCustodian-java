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
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.RetailCustomerService;
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
public class UsagePointRESTController {

	@Autowired
	private UsagePointService usagePointService;

	@Autowired
	private RetailCustomerService retailCustomerService;

	@Autowired
	private ExportService exportService;

	@Autowired
	private ResourceService resourceService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
	}

	// first the RESTful Interface to the ROOT Objects
	@RequestMapping(value = Routes.ROOT_USAGE_POINT_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		try {

			exportService.exportUsagePoints(response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	@RequestMapping(value = Routes.ROOT_USAGE_POINT_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		try {
			exportService.exportUsagePoint(usagePointId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.ROOT_USAGE_POINT_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {
		try {
			UsagePoint usagePoint = this.usagePointService
					.importResource(stream);
			exportService.exportUsagePoint(usagePoint.getId(),
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.ROOT_USAGE_POINT_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params, InputStream stream) {
		UsagePoint usagePoint = usagePointService.findById(usagePointId);

		if (usagePoint != null) {
			try {

				UsagePoint newUsagePoint = usagePointService
						.importResource(stream);
				usagePoint.merge(newUsagePoint);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	@RequestMapping(value = Routes.ROOT_USAGE_POINT_MEMBER, method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(HttpServletResponse response,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) {

		try {
			resourceService.deleteById(usagePointId, UsagePoint.class);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	// now the RESTful Interface to the XPath Objects
	//
	@RequestMapping(value = Routes.USAGE_POINT_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		try {
			exportService.exportUsagePoints(retailCustomerId,
					response.getOutputStream(), new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.USAGE_POINT_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		try {
			exportService.exportUsagePoint(retailCustomerId, usagePointId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.USAGE_POINT_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {


		try {
			RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
			UsagePoint usagePoint = this.usagePointService.importResource(stream);

			usagePointService.associateByUUID(retailCustomer, usagePoint.getUUID());
			
			exportService.exportUsagePoint(retailCustomerId, usagePoint.getId(), response.getOutputStream(), new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.USAGE_POINT_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params, InputStream stream) {

		try {

			Long id = resourceService.findIdByXPath(retailCustomerId,
					usagePointId, UsagePoint.class);
			UsagePoint usagePoint = resourceService.findById(id,
					UsagePoint.class);
			UsagePoint newUsagePoint = usagePointService.importResource(stream);
			usagePoint.merge(newUsagePoint);
		} catch (Exception e) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	@RequestMapping(value = Routes.USAGE_POINT_MEMBER, method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) {

		try {
			resourceService.deleteByXPathId(retailCustomerId, usagePointId,
					UsagePoint.class);

		} catch (Exception e) {
			System.out.printf("****Delete Error: %s - %s\n", UsagePoint.class,
					e.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	public void setUsagePointService(UsagePointService usagePointService) {
		this.usagePointService = usagePointService;
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
