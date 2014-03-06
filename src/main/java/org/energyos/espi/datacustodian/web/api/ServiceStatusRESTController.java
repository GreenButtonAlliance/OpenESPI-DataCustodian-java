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
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.energyos.espi.common.domain.ApplicationInformation;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.models.atom.DateTimeType;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.syndication.io.FeedException;

@Controller
public class ServiceStatusRESTController {
	
	 @Autowired
     ResourceService resourceService;

	// ROOT RESTful Forms
	//
	@RequestMapping(value = Routes.ROOT_SERVICE_STATUS, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void index(HttpServletResponse response,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {
		
		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		ApplicationInformation applicationInformation = resourceService.findById(1L,  ApplicationInformation.class);
    	DateTimeType updated = DateConverter.toDateTimeType(new Date());
        String temp = updated.getValue().toXMLFormat();
    	String uuid = UUID.randomUUID().toString();
    	
		response.getOutputStream().println("<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		response.getOutputStream().println("<id>" + uuid + "</id>");
		response.getOutputStream().println("<title>Service Status</title>");
		response.getOutputStream().println("<description>Service Status: " + 
		                                    applicationInformation.getDataCustodianApplicationStatus() +
		                                    "</description>");
		response.getOutputStream().println("<updated>"+ temp + "</updated>");
		response.getOutputStream().println("</feed>");
		
		}

}
