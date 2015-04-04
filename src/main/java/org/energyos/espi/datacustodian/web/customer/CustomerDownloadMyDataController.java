/*
 * Copyright 2013,2014, 2015 EnergyOS.org
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

package org.energyos.espi.datacustodian.web.customer;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.utils.ExportFilter;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sun.syndication.io.FeedException;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
public class CustomerDownloadMyDataController extends BaseController {

	@Autowired
	private ExportService exportService;

	@RequestMapping(value = Routes.RETAIL_CUSTOMER_DOWNLOAD_MY_DATA, method = RequestMethod.GET)
	public void downloadMyData(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {
		response.setContentType(MediaType.TEXT_HTML_VALUE);
		response.addHeader("Content-Disposition",
				"attachment; filename=GreenButtonDownload.xml");
		try {

			exportService.exportUsagePointFull(0L, retailCustomerId,
					usagePointId, response.getOutputStream(), new ExportFilter(
							params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@RequestMapping(value = Routes.RETAIL_CUSTOMER_DOWNLOAD_MY_DATA_COLLECTION, method = RequestMethod.GET)
	public void downloadMyDataCollection(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		response.setContentType(MediaType.TEXT_HTML_VALUE);
		response.addHeader("Content-Disposition",
				"attachment; filename=GreenButtonDownload.xml");
		try {
			// TODO -- need authorization hook
			exportService.exportUsagePointsFull(0L, retailCustomerId,
					response.getOutputStream(), new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	public ExportService getExportService() {
		return this.exportService;
	}
}