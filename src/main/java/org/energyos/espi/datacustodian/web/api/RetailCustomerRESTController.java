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

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ImportService;
import org.energyos.espi.common.service.RetailCustomerService;
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
public class RetailCustomerRESTController {

	@Autowired
	private ImportService importService;

	@Autowired
	private RetailCustomerService retailCustomerService;

	@Autowired
	private UsagePointService usagePointService;

	@Autowired
	private ExportService exportService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
	}

	// ROOT and XPath are the same for this one.
	//
	@RequestMapping(value = Routes.RETAIL_CUSTOMER_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			exportService.exportRetailCustomers(response.getOutputStream(),
					new ExportFilter(params));
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
	public void show(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {
		
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			exportService.exportRetailCustomer(retailCustomerId,
					response.getOutputStream(), new ExportFilter(params));
		} catch (Exception e) {
			System.out
					.printf("***** Error Caused by RetailCustomer.x.IndentifiedObject need: %s",
							e.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	@RequestMapping(value = Routes.RETAIL_CUSTOMER_COLLECTION, method = RequestMethod.POST, consumes = "application/atom+xml", produces = "application/atom+xml")
	@ResponseBody
	public void create(HttpServletResponse response,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {
		
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		try {
			RetailCustomer retailCustomer = this.retailCustomerService
					.importResource(stream);
			exportService.exportTimeConfiguration(retailCustomer.getId(),
					response.getOutputStream(), new ExportFilter(
							new HashMap<String, String>()));
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

	public void setRetailCustomerService(
			RetailCustomerService retailCustomerService) {
		this.retailCustomerService = retailCustomerService;
	}

	public void setUsagePointService(UsagePointService usagePointService) {
		this.usagePointService = usagePointService;
	}

	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	public void setImportService(ImportService importService) {
		this.importService = importService;
	}
}
