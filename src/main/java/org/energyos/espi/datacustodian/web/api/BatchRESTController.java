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
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ImportService;
import org.energyos.espi.common.service.NotificationService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.utils.ExportFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sun.syndication.io.FeedException;

@Controller
public class BatchRESTController {

	@Autowired
	private ImportService importService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private RetailCustomerService retailCustomerService;

	@Autowired
	private ExportService exportService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
	}

	@RequestMapping(value = Routes.BATCH_UPLOAD_MY_DATA, method = RequestMethod.POST, consumes = "application/xml", produces = "application/atom+xml")
	@ResponseBody
	public void upload(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {
		try {
			RetailCustomer retailCustomer = retailCustomerService
					.findById(retailCustomerId);

			importService.importData(stream, retailCustomerId);

			// do any notifications

			notificationService.notify(retailCustomer,
					importService.getMinUpdated(),
					importService.getMaxUpdated());

		} catch (Exception e) {
			System.out.printf("**** Batch Import Error: %s\n", e.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	@RequestMapping(value = Routes.BATCH_DOWNLOAD_MY_DATA_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void download_collection(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.addHeader("Content-Disposition",
				"attachment; filename=GreenButtonDownload.xml");
		try {
			exportService.exportUsagePointsFull(retailCustomerId,
					response.getOutputStream(), new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	@RequestMapping(value = Routes.BATCH_DOWNLOAD_MY_DATA_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void download_member(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.addHeader("Content-Disposition",
				"attachment; filename=GreenButtonDownload.xml");
		try {
			exportService.exportUsagePointFull(retailCustomerId, usagePointId,
					response.getOutputStream(), new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	@Transactional(readOnly = true)
	@RequestMapping(value = Routes.BATCH_SUBSCRIPTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void subscription(HttpServletResponse response,
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.addHeader("Content-Disposition",
				"attachment; filename=GreenButtonDownload.xml");
		try {
			exportService.exportBatchSubscription(subscriptionId,
					response.getOutputStream(), new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	@RequestMapping(value = Routes.BATCH_BULK_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void bulk(HttpServletResponse response, @PathVariable Long bulkId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.addHeader("Content-Disposition",
				"attachment; filename=GreenButtonDownload.xml");
		try {
			exportService.exportBatchBulk(bulkId, response.getOutputStream(),
					new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	public void setRetailCustomerService(
			RetailCustomerService retailCustomerService) {
		this.retailCustomerService = retailCustomerService;
	}

	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	public void setImportService(ImportService importService) {
		this.importService = importService;
	}
}
