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
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.common.service.AuthorizationService;
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
public class UsagePointRESTController {

	@Autowired
	private UsagePointService usagePointService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private RetailCustomerService retailCustomerService;

	@Autowired
	private ExportService exportService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private AuthorizationService authorizationService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
	}

	// first the RESTful Interface to the ROOT Objects
	@RequestMapping(value = Routes.ROOT_USAGE_POINT_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {

			exportService.exportUsagePoints_Root(subscriptionId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	@RequestMapping(value = Routes.ROOT_USAGE_POINT_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			exportService.exportUsagePoint_Root(subscriptionId, usagePointId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.ROOT_USAGE_POINT_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			UsagePoint usagePoint = this.usagePointService
					.importResource(stream);
			exportService.exportUsagePoint_Root(subscriptionId,
					usagePoint.getId(), response.getOutputStream(),
					new ExportFilter(params));
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
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			Subscription subscription = subscriptionService
					.findById(subscriptionId);
			Authorization authorization = subscription.getAuthorization();
			RetailCustomer retailCustomer = authorization.getRetailCustomer();
			Long retailCustomerId = retailCustomer.getId();

			exportService.exportUsagePoints(subscriptionId, retailCustomerId,
					response.getOutputStream(), new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.USAGE_POINT_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);
			exportService.exportUsagePoint(subscriptionId, retailCustomerId,
					usagePointId, response.getOutputStream(), new ExportFilter(
							params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.USAGE_POINT_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			Subscription subscription = subscriptionService
					.findById(subscriptionId);
			Authorization authorization = subscription.getAuthorization();
			RetailCustomer retailCustomer = authorization.getRetailCustomer();
			Long retailCustomerId = retailCustomer.getId();

			UsagePoint usagePoint = this.usagePointService
					.importResource(stream);

			usagePointService.associateByUUID(retailCustomer,
					usagePoint.getUUID());

			exportService.exportUsagePoint(subscriptionId, retailCustomerId,
					usagePoint.getId(), response.getOutputStream(),
					new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.USAGE_POINT_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@RequestParam Map<String, String> params, InputStream stream) {

		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);
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
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) {

		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);
			resourceService.deleteByXPathId(retailCustomerId, usagePointId,
					UsagePoint.class);

		} catch (Exception e) {
			System.out.printf("****Delete Error: %s - %s\n", UsagePoint.class,
					e.toString());
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

	public void setUsagePointService(UsagePointService usagePointService) {
		this.usagePointService = usagePointService;
	}

	public UsagePointService getUsagePointService() {
		return this.usagePointService;
	}

	public void setSubscriptionService(SubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	public SubscriptionService getSubscriptionService(
			SubscriptionService subscriptionService) {
		return this.subscriptionService;
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

	public void setAuthorizationService(
			AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	public AuthorizationService getAuthorizationService() {
		return this.authorizationService;
	}

}
