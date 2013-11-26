package org.energyos.espi.datacustodian.web.api;
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

import com.sun.syndication.io.FeedException;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.datacustodian.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

@Controller
public class SubscriptionRESTController {

    @Autowired
    private ExportService exportService;

    @RequestMapping(value = Routes.DATA_CUSTODIAN_SUBSCRIPTION, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void show(HttpServletResponse response, @PathVariable String subscriptionHashedId) throws FeedException, IOException, InterruptedException, JAXBException, XMLStreamException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);

        exportService.exportSubscription(subscriptionHashedId, response.getOutputStream());
    }

    public void setExportService(ExportService exportService) {
        this.exportService = exportService;
    }
}
