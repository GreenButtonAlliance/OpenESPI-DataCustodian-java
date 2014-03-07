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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.common.utils.ExportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.syndication.io.FeedException;

@Controller
public class SubscriptionRESTController {

	@Autowired
	private ExportService exportService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private RetailCustomerService retailCustomerService;

	// original Pivotal Code
	//
	/*
	 * @RequestMapping(value = Routes.DATA_CUSTODIAN_SUBSCRIPTION, method =
	 * RequestMethod, produces = "application/atom+xml") @ResponseBody
	 * 
	 * @ResponseStatus(HttpStatus.OK) public void show(HttpServletResponse
	 * response, @PathVariable String subscriptionHashedId, @RequestParam
	 * Map<String, String> params) throws FeedException, IOException,
	 * InterruptedException, JAXBException, XMLStreamException {
	 * response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
	 * exportService.exportSubscription(subscriptionHashedId,
	 * response.getOutputStream(), new ExportFilter(params)); }
	 */

	// ROOT RESTful Forms
	//
	@RequestMapping(value = Routes.ROOT_SUBSCRIPTION_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		exportService.exportSubscriptions(response.getOutputStream(),
				new ExportFilter(params));
	}

	@RequestMapping(value = Routes.ROOT_SUBSCRIPTION_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		exportService.exportSubscription(subscriptionId,
				response.getOutputStream(), new ExportFilter(params));
	}

	//
	//
	@RequestMapping(value = Routes.ROOT_SUBSCRIPTION_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			Subscription subscription = this.subscriptionService
					.importResource(stream);
			exportService.exportSubscription(subscription.getId(),
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	//

	@RequestMapping(value = Routes.ROOT_SUBSCRIPTION_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {
		Subscription subscription = subscriptionService
				.findById(subscriptionId);

		if (subscription != null) {
			try {

				Subscription newSubscription = subscriptionService
						.importResource(stream);
				subscription.merge(newSubscription);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	@RequestMapping(value = Routes.ROOT_SUBSCRIPTION_MEMBER, method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response,
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {
		try {
			resourceService.deleteById(subscriptionId, Subscription.class);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	// XPath RESTful forms
	//
	@RequestMapping(value = Routes.SUBSCRIPTION_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		exportService.exportSubscriptions(retailCustomerId,
				response.getOutputStream(), new ExportFilter(params));
	}

	//
	//
	@RequestMapping(value = Routes.SUBSCRIPTION_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void show(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		exportService.exportSubscription(retailCustomerId, subscriptionId,
				response.getOutputStream(), new ExportFilter(params));
	}

	//
	//
	@RequestMapping(value = Routes.SUBSCRIPTION_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params,
			@RequestBody ByteArrayInputStream stream) throws IOException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			Subscription subscription = this.subscriptionService
					.importResource(stream);
			retailCustomerService.associateByUUID(retailCustomerId,
					subscription.getUUID());
			exportService.exportSubscription(subscription.getId(),
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	//

	@RequestMapping(value = Routes.SUBSCRIPTION_MEMBER, method = RequestMethod.PUT, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void update(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {
		Subscription subscription = subscriptionService.findById(
				retailCustomerId, subscriptionId);

		if (subscription != null) {
			try {

				Subscription newSubscription = subscriptionService
						.importResource(stream);
				subscription.merge(newSubscription);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	@RequestMapping(value = Routes.SUBSCRIPTION_MEMBER, method = RequestMethod.DELETE)
	public void delete(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {
		try {
			resourceService.deleteByXPathId(retailCustomerId, subscriptionId,
					Subscription.class);

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	public void setSubscriptionService(SubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public void setRetailCustomerService(
			RetailCustomerService retailCustomerService) {
		this.retailCustomerService = retailCustomerService;
	}
}
