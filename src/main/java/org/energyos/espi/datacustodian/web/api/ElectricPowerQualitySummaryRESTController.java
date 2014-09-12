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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.energyos.espi.common.domain.Authorization;
import org.energyos.espi.common.domain.ElectricPowerQualitySummary;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.ElectricPowerQualitySummaryService;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.utils.ExportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
public class ElectricPowerQualitySummaryRESTController {

	@Autowired
	private ElectricPowerQualitySummaryService electricPowerQualitySummaryService;

	@Autowired
	private UsagePointService usagePointService;

	@Autowired
	private RetailCustomerService retailCustomerService;

	@Autowired
	private ExportService exportService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private AuthorizationService authorizationService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
	}

	// ROOT RESTful forms
	//
	@RequestMapping(value = Routes.ROOT_ELECTRIC_POWER_QUALITY_SUMMARY_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		exportService.exportElectricPowerQualitySummarys_Root(subscriptionId,
				response.getOutputStream(), new ExportFilter(params));
	}

	//
	//
	@RequestMapping(value = Routes.ROOT_ELECTRIC_POWER_QUALITY_SUMMARY_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long electricPowerQualitySummaryId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);

		try {
			exportService.exportElectricPowerQualitySummary_Root(
					subscriptionId, electricPowerQualitySummaryId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.ROOT_ELECTRIC_POWER_QUALITY_SUMMARY_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);

		try {
			ElectricPowerQualitySummary electricPowerQualitySummary = this.electricPowerQualitySummaryService
					.importResource(stream);
			exportService.exportElectricPowerQualitySummary_Root(
					subscriptionId, electricPowerQualitySummary.getId(),
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.ROOT_ELECTRIC_POWER_QUALITY_SUMMARY_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long electricPowerQualitySummaryId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {
		ElectricPowerQualitySummary electricPowerQualitySummary = electricPowerQualitySummaryService
				.findById(electricPowerQualitySummaryId);

		if (electricPowerQualitySummary != null) {
			try {

				ElectricPowerQualitySummary newElectricPowerQualitySummary = electricPowerQualitySummaryService
						.importResource(stream);
				electricPowerQualitySummary
						.merge(newElectricPowerQualitySummary);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	@RequestMapping(value = Routes.ROOT_ELECTRIC_POWER_QUALITY_SUMMARY_MEMBER, method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response,
			@PathVariable Long electricPowerQualitySummaryId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {

		try {
			resourceService.deleteById(electricPowerQualitySummaryId,
					ElectricPowerQualitySummary.class);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	// XPath RESTful forms
	//
	@RequestMapping(value = Routes.ELECTRIC_POWER_QUALITY_SUMMARY_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);

		Long retailCustomerId = subscriptionService.findRetailCustomerId(
				subscriptionId, usagePointId);

		exportService.exportElectricPowerQualitySummarys(subscriptionId,
				retailCustomerId, usagePointId, response.getOutputStream(),
				new ExportFilter(params));
	}

	@RequestMapping(value = Routes.ELECTRIC_POWER_QUALITY_SUMMARY_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@PathVariable Long electricPowerQualitySummaryId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);

		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			exportService.exportElectricPowerQualitySummary(subscriptionId,
					retailCustomerId, usagePointId,
					electricPowerQualitySummaryId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.ELECTRIC_POWER_QUALITY_SUMMARY_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		Long retailCustomerId = subscriptionService.findRetailCustomerId(
				subscriptionId, usagePointId);

		if (null != resourceService.findIdByXPath(retailCustomerId,
				usagePointId, UsagePoint.class)) {
			try {

				UsagePoint usagePoint = usagePointService
						.findById(usagePointId);
				ElectricPowerQualitySummary electricPowerQualitySummary = electricPowerQualitySummaryService
						.importResource(stream);
				electricPowerQualitySummaryService.associateByUUID(usagePoint,
						electricPowerQualitySummary.getUUID());
				exportService.exportElectricPowerQualitySummary(subscriptionId,
						retailCustomerId, usagePointId,
						electricPowerQualitySummary.getId(),
						response.getOutputStream(), new ExportFilter(params));
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	//

	@RequestMapping(value = Routes.ELECTRIC_POWER_QUALITY_SUMMARY_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@PathVariable Long electricPowerQualitySummaryId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {

		ElectricPowerQualitySummary electricPowerQualitySummary = electricPowerQualitySummaryService
				.findById(electricPowerQualitySummaryId);

		if (electricPowerQualitySummary != null) {
			try {
				Long retailCustomerId = subscriptionService
						.findRetailCustomerId(subscriptionId, usagePointId);

				if (null != resourceService.findIdByXPath(retailCustomerId,
						usagePointId, electricPowerQualitySummaryId,
						ElectricPowerQualitySummary.class)) {
					ElectricPowerQualitySummary newElectricPowerQualitySummary = electricPowerQualitySummaryService
							.importResource(stream);
					electricPowerQualitySummary
							.merge(newElectricPowerQualitySummary);
				}

			} catch (Exception e) {

				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	@RequestMapping(value = Routes.ELECTRIC_POWER_QUALITY_SUMMARY_MEMBER, method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@PathVariable Long electricPowerQualitySummaryId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {

		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			resourceService.deleteByXPathId(retailCustomerId, usagePointId,
					electricPowerQualitySummaryId,
					ElectricPowerQualitySummary.class);

		} catch (Exception e) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private Long getSubscriptionId(HttpServletRequest request) {
		String token = request.getHeader("authorization");
		Long subscriptionId = 0L;

		if (token != null) {
			token = token.replace("Bearer ", "");
			Authorization authorization = authorizationService
					.findByAccessToken(token);
			if (authorization != null) {
				Subscription subscription = authorization.getSubscription();
				if (subscription != null) {
					subscriptionId = subscription.getId();
				}
			}
		}

		return subscriptionId;

	}

	public void setElectricPowerQualitySummaryService(
			ElectricPowerQualitySummaryService electricPowerQualitySummaryService) {
		this.electricPowerQualitySummaryService = electricPowerQualitySummaryService;
	}

	public ElectricPowerQualitySummaryService getElectricPowerQualitySummaryService() {
		return this.electricPowerQualitySummaryService;
	}

	public void setUsagePointService(UsagePointService usagePointService) {
		this.usagePointService = usagePointService;
	}

	public UsagePointService getUsagePointService() {
		return this.usagePointService;
	}

	public void setRetailCustomerService(
			RetailCustomerService retailCustomerService) {
		this.retailCustomerService = retailCustomerService;
	}

	public RetailCustomerService getRetailCustomerService() {
		return this.retailCustomerService;
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

	public void setSubscriptionService(SubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	public SubscriptionService getSubscriptionService() {
		return this.subscriptionService;
	}

	public void setAuthorizationService(
			AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	public AuthorizationService getAuthorizationService() {
		return this.authorizationService;
	}

}
