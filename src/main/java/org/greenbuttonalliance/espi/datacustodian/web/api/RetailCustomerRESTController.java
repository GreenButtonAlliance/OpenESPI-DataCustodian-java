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
import org.greenbuttonalliance.espi.common.domain.Authorization;
import org.greenbuttonalliance.espi.common.domain.RetailCustomer;
import org.greenbuttonalliance.espi.common.domain.Routes;
import org.greenbuttonalliance.espi.common.domain.Subscription;
import org.greenbuttonalliance.espi.common.service.*;
import org.greenbuttonalliance.espi.common.utils.ExportFilter;
import org.greenbuttonalliance.espi.datacustodian.utils.VerifyURLParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RetailCustomerRESTController {

	@Autowired
	private ImportService importService;

	@Autowired
	private RetailCustomerService retailCustomerService;

	@Autowired
	private UsagePointService usagePointService;

	@Autowired
	private ExportService exportService;

	@Autowired
	private AuthorizationService authorizationService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
	}

	// ROOT and XPath are the same for this one.
	//
	@RequestMapping(value = Routes.RETAIL_CUSTOMER_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.RETAIL_CUSTOMER_COLLECTION, params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			exportService.exportRetailCustomers(subscriptionId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			System.out
					.printf("***** Error Caused by RetailCustomer.x.IndentifiedObject need: %s",
							e.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	//
	//
	@RequestMapping(value = Routes.RETAIL_CUSTOMER_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.RETAIL_CUSTOMER_MEMBER, params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			exportService.exportRetailCustomer(subscriptionId,
					retailCustomerId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			System.out
					.printf("***** Error Caused by RetailCustomer.x.IndentifiedObject need: %s",
							e.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	@RequestMapping(value = Routes.RETAIL_CUSTOMER_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			RetailCustomer retailCustomer = this.retailCustomerService
					.importResource(stream);
			exportService.exportRetailCustomer(subscriptionId,
					retailCustomer.getId(), response.getOutputStream(),
					new ExportFilter(new HashMap<String, String>()));
		} catch (Exception e) {
			System.out
					.printf("***** Error Caused by RetailCustomer.x.IndentifiedObject need: %s",
							e.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.RETAIL_CUSTOMER_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long applicationInformationId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {
		RetailCustomer retailCustomer = retailCustomerService
				.findById(applicationInformationId);

		if (retailCustomer != null) {
			try {

				RetailCustomer newRetailCustomer = retailCustomerService
						.importResource(stream);
				retailCustomer.merge(newRetailCustomer);
			} catch (Exception e) {
				System.out
						.printf("***** Error Caused by RetailCustomer.x.IndentifiedObject need: %s",
								e.toString());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	@RequestMapping(value = Routes.RETAIL_CUSTOMER_MEMBER, method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response,
			@PathVariable Long applicationInformationId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {

		RetailCustomer retailCustomer = retailCustomerService
				.findById(applicationInformationId);

		if (retailCustomer != null) {
			retailCustomerService.delete(retailCustomer);
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

	public void setImportService(ImportService importService) {
		this.importService = importService;
	}

	public ImportService getImportService() {
		return this.importService;
	}

	public void setRetailCustomerService(
			RetailCustomerService retailCustomerService) {
		this.retailCustomerService = retailCustomerService;
	}

	public RetailCustomerService getRetailcustomerService() {
		return this.retailCustomerService;
	}

	public void setUsagePointService(UsagePointService usagePointService) {
		this.usagePointService = usagePointService;
	}

	public UsagePointService getUsagepointService() {
		return this.usagePointService;
	}

	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	public ExportService getExportService() {
		return this.exportService;
	}

	public void setAuthorizationService(
			AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	public AuthorizationService getAuthorizationService() {
		return this.authorizationService;
	}

}
