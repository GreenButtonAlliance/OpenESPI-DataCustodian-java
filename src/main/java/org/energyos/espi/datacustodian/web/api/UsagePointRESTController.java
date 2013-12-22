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

import com.sun.syndication.io.FeedException;

import org.energyos.espi.common.domain.ApplicationInformation;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.utils.ExportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Controller
public class UsagePointRESTController {

    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private RetailCustomerService retailCustomerService;
    @Autowired
    private ExportService exportService;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleGenericException() {}

    // ROOT RESTful APIs
    @RequestMapping(value = Routes.ROOT_USAGE_POINT_COLLECTION, method = RequestMethod.GET)
    public void index(HttpServletResponse response, @RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportUsagePoints(response.getOutputStream(), new ExportFilter(params));
    }

    @RequestMapping(value = Routes.ROOT_USAGE_POINT_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response,
		     @PathVariable Long usagePointId,
    		     @RequestParam Map<String, String> params)
	throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportUsagePoint(usagePointId, response.getOutputStream(), new ExportFilter(params));
    }

    @RequestMapping(value = Routes.ROOT_USAGE_POINT_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response, @RequestParam Map<String, String> params, InputStream stream) throws IOException {
        try {
            UsagePoint usagePoint = this.usagePointService.importResource(stream);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }


    @RequestMapping(value = Routes.ROOT_USAGE_POINT_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response, @PathVariable Long usagePointId,
    		@RequestParam Map<String, String> params, InputStream stream) {
    	UsagePoint usagePoint = usagePointService.findById(usagePointId);
    	 
        if (usagePoint != null) {
            try {
            	
                UsagePoint newUsagePoint = usagePointService.importResource(stream);
                usagePoint.merge(newUsagePoint);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    @RequestMapping(value = Routes.ROOT_USAGE_POINT_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, @PathVariable Long usagePointId,
    		@RequestParam Map<String, String> params) {
    	UsagePoint usagePoint = usagePointService.findById(usagePointId);

        if (usagePoint != null) {
        	usagePointService.delete(usagePoint);
        }
    }



    // XPath RESTful APIs
    //
    @RequestMapping(value = Routes.USAGE_POINT_COLLECTION, method = RequestMethod.GET)
    public void index(HttpServletResponse response, @PathVariable Long retailCustomerId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportUsagePoints(retailCustomerId, response.getOutputStream(), new ExportFilter(params));
    }

    @RequestMapping(value = Routes.USAGE_POINT_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response, @PathVariable Long retailCustomerId, @PathVariable Long usagePointId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportUsagePoint(retailCustomerId, usagePointId, response.getOutputStream(), new ExportFilter(params));
    }

    @RequestMapping(value = Routes.USAGE_POINT_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response, @PathVariable Long retailCustomerId,
    		@RequestParam Map<String, String> params, InputStream stream) throws IOException {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        try {
            UsagePoint usagePoint = this.usagePointService.importResource(stream);
            // TODO would like to just do a .add
            // retailCustomerService.add(usagePoint);
            usagePointService.associateByUUID(retailCustomer, usagePoint.getUUID());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }


    @RequestMapping(value = Routes.USAGE_POINT_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response, @PathVariable Long retailCustomerId, @PathVariable Long usagePointId,
    		@RequestParam Map<String, String> params, InputStream stream) {

    	RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
    	UsagePoint usagePoint = usagePointService.findById(retailCustomerId, usagePointId);
    	 
        if (usagePoint != null) {
            try {
            	
                UsagePoint newUsagePoint = usagePointService.importResource(stream);
                usagePoint.merge(newUsagePoint);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    @RequestMapping(value = Routes.USAGE_POINT_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, @PathVariable Long retailCustomerId, @PathVariable Long usagePointId,
    		@RequestParam Map<String, String> params) {
    	UsagePoint usagePoint = usagePointService.findById(retailCustomerId, usagePointId);

        if (usagePoint != null) {
        	usagePointService.delete(usagePoint);
        }
    }

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }

    public void setRetailCustomerService(RetailCustomerService retailCustomerService) {
        this.retailCustomerService = retailCustomerService;
    }

    public void setExportService(ExportService exportService) {
    	this.exportService = exportService;
    }
}
