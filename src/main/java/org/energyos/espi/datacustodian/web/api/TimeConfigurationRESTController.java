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

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.TimeConfiguration;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.TimeConfigurationService;
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
public class TimeConfigurationRESTController {

    @Autowired
    private TimeConfigurationService timeConfigurationService;
    @Autowired
    private RetailCustomerService retailCustomerService;
    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private ExportService exportService;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleGenericException() {}

    // ROOT RESTFul Forms
    //
    @RequestMapping(value = Routes.ROOT_TIME_CONFIGURATION_COLLECTION, method = RequestMethod.GET)
    public void index(HttpServletResponse response,
 		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportTimeConfigurations( response.getOutputStream(), new ExportFilter(params));
    }

    // 
    //
    @RequestMapping(value = Routes.ROOT_TIME_CONFIGURATION_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response, @PathVariable long timeConfigurationId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        try {
            exportService.exportTimeConfiguration(timeConfigurationId, response.getOutputStream(), new ExportFilter(params));
                    } catch (Exception e) {
              response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          } 
        }

    // 
    //
    @RequestMapping(value = Routes.ROOT_TIME_CONFIGURATION_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response, 
    		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        try {
            TimeConfiguration timeConfiguration = this.timeConfigurationService.importResource(stream);
            exportService.exportTimeConfiguration(timeConfiguration.getId(), response.getOutputStream(), new ExportFilter(null));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    //
    @RequestMapping(value = Routes.ROOT_TIME_CONFIGURATION_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response, 
    		@PathVariable long timeConfigurationId,
    		@RequestParam Map<String, String> params, 
    		InputStream stream) {
    	
    	// NOTE: that import is going to do the put action IF there is an existing UUID equivalence. That overrides
    	// the timeConfiguraitonId used above. I don't think that is the right behavior ... 
    	// this would be the behavior of /DataCustodian/Import.  The RESTful PUT should possibly fail if there 
    	// is a missmatch of UUID and ID. 

    	try {
    		timeConfigurationService.importResource(stream);
    	} catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    	}
    	/*
        TimeConfiguration existingTimeConfiguration;
        existingTimeConfiguration = timeConfigurationService.findById(timeConfigurationId);

        if (existingTimeConfiguration != null) {
            try {
                TimeConfiguration timeConfiguration = timeConfigurationService.importResource(stream);
	            existingTimeConfiguration.merge(timeConfiguration);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        */
    }

    @RequestMapping(value = Routes.ROOT_TIME_CONFIGURATION_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, 
    		@PathVariable long timeConfigurationId,
    		@RequestParam Map<String, String> params, 
    		InputStream stream) {
        this.timeConfigurationService.deleteById(timeConfigurationId);
    }

    // XPath RESTful Forms
    //
    @RequestMapping(value = Routes.TIME_CONFIGURATION_COLLECTION, method = RequestMethod.GET)
    public void index(HttpServletResponse response, @PathVariable long retailCustomerId, @PathVariable long usagePointId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportTimeConfigurations(retailCustomerId, usagePointId, response.getOutputStream(), new ExportFilter(params));
    }

    // 
    //
    @RequestMapping(value = Routes.TIME_CONFIGURATION_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response, @PathVariable long retailCustomerId, @PathVariable long usagePointId, @PathVariable long timeConfigurationId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);

        try {
            exportService.exportTimeConfiguration(retailCustomerId, usagePointId, timeConfigurationId, response.getOutputStream(), new ExportFilter(params));
                    } catch (Exception e) {
              response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          } 
    }

    // 
    //
    @RequestMapping(value = Routes.TIME_CONFIGURATION_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response, 
    		@PathVariable long retailCustomerId, 
    		@PathVariable long usagePointId,
    		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException {
    	
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
    	RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        UsagePoint usagePoint = usagePointService.findById(usagePointId);

        try {
            TimeConfiguration timeConfiguration = this.timeConfigurationService.importResource(stream);
            timeConfigurationService.associateByUUID(usagePoint, timeConfiguration.getUUID());
            exportService.exportTimeConfiguration(retailCustomerId, usagePointId, timeConfiguration.getId(), response.getOutputStream(), new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    //
    @RequestMapping(value = Routes.TIME_CONFIGURATION_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long timeConfigurationId,
    		@RequestParam Map<String, String> params, 
    		InputStream stream) {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        UsagePoint usagePoint = usagePointService.findById(usagePointId);
        TimeConfiguration existingTimeConfiguration;

        existingTimeConfiguration = timeConfigurationService.findById(timeConfigurationId);

        if (existingTimeConfiguration != null) {
            try {
                TimeConfiguration timeConfiguration = timeConfigurationService.importResource(stream);
                existingTimeConfiguration.merge(timeConfiguration);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @RequestMapping(value = Routes.TIME_CONFIGURATION_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long timeConfigurationId,
    		@RequestParam Map<String, String> params, 
    		InputStream stream) {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        UsagePoint usagePoint = usagePointService.findById(usagePointId);

        this.timeConfigurationService.deleteById(timeConfigurationId);
    }

    public void setRetailCustomerService(RetailCustomerService retailCustomerService) {
        this.retailCustomerService = retailCustomerService;
    }

    public void setExportService(ExportService exportService) {
        this.exportService = exportService;
    }
}
