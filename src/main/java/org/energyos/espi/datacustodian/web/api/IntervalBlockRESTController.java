/*
 * Copyright 2013 EnergyOS.org
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
import org.energyos.espi.common.domain.IntervalBlock;
import org.energyos.espi.common.domain.MeterReading;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.IntervalBlockService;
import org.energyos.espi.common.service.MeterReadingService;
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
public class IntervalBlockRESTController {

	@Autowired
	private IntervalBlockService intervalBlockService;

	@Autowired
	private RetailCustomerService retailCustomerService;

	@Autowired
	private UsagePointService usagePointService;

	@Autowired
	private MeterReadingService meterReadingService;

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
	@RequestMapping(value = Routes.ROOT_INTERVAL_BLOCK_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		exportService.exportIntervalBlocks_Root(subscriptionId,
				response.getOutputStream(), new ExportFilter(params));
	}

	//
	//
	@RequestMapping(value = Routes.ROOT_INTERVAL_BLOCK_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long intervalBlockId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			exportService.exportIntervalBlock_Root(subscriptionId,
					intervalBlockId, response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	//
	//
	@RequestMapping(value = Routes.ROOT_INTERVAL_BLOCK_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		Long subscriptionId = getSubscriptionId(request);

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			IntervalBlock intervalBlock = this.intervalBlockService
					.importResource(stream);
			exportService.exportIntervalBlock_Root(subscriptionId,
					intervalBlock.getId(), response.getOutputStream(),
					new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	//
	@RequestMapping(value = Routes.ROOT_INTERVAL_BLOCK_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long intervalBlockId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {

		IntervalBlock intervalBlock = intervalBlockService
				.findById(intervalBlockId);

		if (intervalBlock != null) {
			try {
				intervalBlock
						.merge(intervalBlockService.importResource(stream));
				intervalBlockService.persist(intervalBlock);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	@RequestMapping(value = Routes.ROOT_INTERVAL_BLOCK_MEMBER, method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response,
			@PathVariable Long intervalBlockId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {

		try {
			resourceService.deleteById(intervalBlockId, IntervalBlock.class);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	// XPath RESTful forms
	//
	@RequestMapping(value = Routes.INTERVAL_BLOCK_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@PathVariable Long meterReadingId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);

		Long retailCustomerId = subscriptionService.findRetailCustomerId(
				subscriptionId, usagePointId);

		exportService.exportIntervalBlocks(subscriptionId, retailCustomerId,
				usagePointId, meterReadingId, response.getOutputStream(),
				new ExportFilter(params));
	}

	//
	//
	@RequestMapping(value = Routes.INTERVAL_BLOCK_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@PathVariable Long meterReadingId,
			@PathVariable Long intervalBlockId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			exportService.exportIntervalBlock(subscriptionId, retailCustomerId,
					usagePointId, meterReadingId, intervalBlockId,
					response.getOutputStream(), new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	//
	//
	@RequestMapping(value = Routes.INTERVAL_BLOCK_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@PathVariable Long meterReadingId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);

		Long retailCustomerId = subscriptionService.findRetailCustomerId(
				subscriptionId, usagePointId);

		if (null != resourceService.findIdByXPath(retailCustomerId,
				usagePointId, meterReadingId, MeterReading.class)) {
			try {
				MeterReading meterReading = resourceService.findById(
						meterReadingId, MeterReading.class);
				IntervalBlock intervalBlock = this.intervalBlockService
						.importResource(stream);
				intervalBlockService.associateByUUID(meterReading,
						intervalBlock.getUUID());
				exportService.exportIntervalBlock(subscriptionId,
						retailCustomerId, usagePointId, meterReadingId,
						intervalBlock.getId(), response.getOutputStream(),
						new ExportFilter(params));

			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	//
	@RequestMapping(value = Routes.INTERVAL_BLOCK_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@PathVariable Long meterReadingId,
			@PathVariable Long intervalBlockId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {

		Long retailCustomerId = subscriptionService.findRetailCustomerId(
				subscriptionId, usagePointId);

		IntervalBlock intervalBlock = intervalBlockService
				.findById(retailCustomerId, usagePointId, meterReadingId,
						intervalBlockId);

		if (intervalBlock != null) {

			try {
				intervalBlock
						.merge(intervalBlockService.importResource(stream));

				resourceService.merge(intervalBlock);

			} catch (Exception e) {

				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.INTERVAL_BLOCK_MEMBER, method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@PathVariable Long meterReadingId,
			@PathVariable Long intervalBlockId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {
		try {
			Long retailCustomerId = subscriptionService.findRetailCustomerId(
					subscriptionId, usagePointId);

			resourceService.deleteByXPathId(retailCustomerId, usagePointId,
					meterReadingId, intervalBlockId, IntervalBlock.class);

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

	public void setIntervalBlockService(
			IntervalBlockService intervalBlockService) {
		this.intervalBlockService = intervalBlockService;
	}

	public IntervalBlockService getIntervalBlockService() {
		return this.intervalBlockService;
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

	public void setMeterReadingService(MeterReadingService meterReadingService) {
		this.meterReadingService = meterReadingService;
	}

	public MeterReadingService getMeterReadingService() {
		return this.meterReadingService;
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
