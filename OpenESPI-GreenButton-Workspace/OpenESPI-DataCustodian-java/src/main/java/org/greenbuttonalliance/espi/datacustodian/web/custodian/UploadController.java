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


package org.greenbuttonalliance.espi.datacustodian.web.custodian;

import org.greenbuttonalliance.espi.common.domain.legacy.Routes;
import org.greenbuttonalliance.espi.common.service.ImportService;
import org.greenbuttonalliance.espi.common.service.NotificationService;
import org.greenbuttonalliance.espi.common.service.UsagePointService;
import org.greenbuttonalliance.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;

@Controller
public class UploadController extends BaseController {

	@Autowired
	private ImportService importService;

	@Autowired
	private UsagePointService usagePointService;

	@Autowired
	private NotificationService notificationService;

	@ModelAttribute("uploadForm")
	public UploadForm uploadForm() {
		return new UploadForm();
	}

	@RequestMapping(value = Routes.DATA_CUSTODIAN_UPLOAD, method = RequestMethod.GET)
	public String upload() {
		return "/custodian/upload";
	}

	@RequestMapping(value = Routes.DATA_CUSTODIAN_UPLOAD, method = RequestMethod.POST)
	public String uploadPost(@ModelAttribute UploadForm uploadForm,
			BindingResult result) throws IOException, JAXBException {
		
		try {
			
			importService.importData(uploadForm.getFile().getInputStream(),
					null);
			return "redirect:/custodian/retailcustomers";
			
		} catch (SAXException e) {
			
			result.addError(new ObjectError("uploadForm",
					e.getMessage()));
			return "/custodian/upload";
				
		} catch (Exception e) {
				
			result.addError(new ObjectError("uploadForm",
						"Unable to process file"));
			return "/custodian/upload";
		} 
	}

	public void setImportService(ImportService importService) {
		this.importService = importService;
	}

	public ImportService getImportService() {
		return this.importService;
	}

	public void setUsagePointService(UsagePointService usagePointService) {
		this.usagePointService = usagePointService;
	}

	public UsagePointService getUsagePointService() {
		return this.usagePointService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public NotificationService getNotificationService() {
		return this.notificationService;
	}

}
