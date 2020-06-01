/*
 *    Copyright (c) 2018-2020 Green Button Alliance, Inc.
 *
 *    Portions copyright (c) 2013-2018 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.greenbuttonalliance.espi.datacustodian.web.api;

import com.sun.syndication.io.FeedException;
import org.greenbuttonalliance.espi.common.domain.*;
import org.greenbuttonalliance.espi.common.service.*;
import org.greenbuttonalliance.espi.common.utils.ExportFilter;
import org.greenbuttonalliance.espi.datacustodian.utils.VerifyURLParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Controller
public class ElectricPowerQualitySummaryRESTController {

	private static final String INVALID_QUERY_PARAMETER = "Request contains invalid query parameter values!";

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

	// ROOT RESTful forms
	//
	@RequestMapping(value = Routes.ROOT_ELECTRIC_POWER_QUALITY_SUMMARY_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.ROOT_ELECTRIC_POWER_QUALITY_SUMMARY_COLLECTION, params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, INVALID_QUERY_PARAMETER);
			return;
		}

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

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.ROOT_ELECTRIC_POWER_QUALITY_SUMMARY_MEMBER, params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, INVALID_QUERY_PARAMETER);
			return;
		}

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

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.ELECTRIC_POWER_QUALITY_SUMMARY_COLLECTION, params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, INVALID_QUERY_PARAMETER);
			return;
		}

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

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.ELECTRIC_POWER_QUALITY_SUMMARY_MEMBER, params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, INVALID_QUERY_PARAMETER);
			return;
		}

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
