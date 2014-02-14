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

package org.energyos.espi.datacustodian.web.custodian;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.service.ImportService;
import org.energyos.espi.common.service.NotificationService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UploadController extends BaseController {

    @Autowired
    private ImportService importService;
    
    @Autowired
    private UsagePointService usagePointService;
    
    @Autowired
    private NotificationService notificationService;

    public void setImportService(ImportService importService) {
        this.importService = importService;
    }

    @ModelAttribute("uploadForm")
    public UploadForm uploadForm() {
        return new UploadForm();
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_UPLOAD, method = RequestMethod.GET)
    public String upload() {
        return "/custodian/upload";
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_UPLOAD, method = RequestMethod.POST)
    public String uploadPost(@ModelAttribute UploadForm uploadForm, BindingResult result) throws IOException, JAXBException {
        try {

            importService.importData(uploadForm.getFile().getInputStream(), null);
        	
            return "redirect:/custodian/retailcustomers";
        } catch (Exception e) {
            result.addError(new ObjectError("uploadForm", "Unable to process file"));
            return "/custodian/upload";
        }
    }
}
