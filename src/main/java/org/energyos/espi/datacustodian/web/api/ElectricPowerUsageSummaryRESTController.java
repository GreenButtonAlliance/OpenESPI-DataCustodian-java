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

import org.energyos.espi.common.domain.IntervalBlock;
import org.energyos.espi.common.domain.MeterReading;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.ElectricPowerUsageSummary;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.IntervalBlockService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.ElectricPowerUsageSummaryService;
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
public class ElectricPowerUsageSummaryRESTController {

    @Autowired
    private ElectricPowerUsageSummaryService electricPowerUsageSummaryService;
    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private RetailCustomerService retailCustomerService;
    @Autowired
    private ExportService exportService;


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleGenericException() {}

    // 
    //
    @RequestMapping(value = Routes.ELECTRIC_POWER_USAGE_SUMMARY_COLLECTION, method = RequestMethod.GET)
    public void index(HttpServletResponse response,
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        exportService.exportElectricPowerUsageSummarys(retailCustomerId, usagePointId, response.getOutputStream(), new ExportFilter(params));
    }

    // 
    //
    @RequestMapping(value = Routes.ELECTRIC_POWER_USAGE_SUMMARY_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long electricPowerUsageSummaryId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        exportService.exportElectricPowerUsageSummary(retailCustomerId, usagePointId, electricPowerUsageSummaryId, response.getOutputStream(), new ExportFilter(params));
    }

    // 
    //
    @RequestMapping(value = Routes.ELECTRIC_POWER_USAGE_SUMMARY_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
    	UsagePoint usagePoint = usagePointService.findById(usagePointId);
        try {
            ElectricPowerUsageSummary electricPowerUsageSummary = this.electricPowerUsageSummaryService.importResource(stream);
            electricPowerUsageSummaryService.associateByUUID(usagePoint, electricPowerUsageSummary.getUUID());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }
    //

    @RequestMapping(value = Routes.ELECTRIC_POWER_USAGE_SUMMARY_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long electricPowerUsageSummaryId,
    		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException, FeedException {
    	RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
    	UsagePoint usagePoint = usagePointService.findById(usagePointId);
    	ElectricPowerUsageSummary electricPowerUsageSummary = electricPowerUsageSummaryService.findById(electricPowerUsageSummaryId);
 
        if (electricPowerUsageSummary != null) {
            try {
            	
                ElectricPowerUsageSummary newElectricPowerUsageSummary = electricPowerUsageSummaryService.importResource(stream);
                electricPowerUsageSummary.merge(newElectricPowerUsageSummary);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    @RequestMapping(value = Routes.ELECTRIC_POWER_USAGE_SUMMARY_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, 
       		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long electricPowerUsageSummaryId,
    		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException, FeedException {
    	RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
    	UsagePoint usagePoint = usagePointService.findById(usagePointId);
    	ElectricPowerUsageSummary electricPowerUsageSummary = electricPowerUsageSummaryService.findById(electricPowerUsageSummaryId);

        if (electricPowerUsageSummary != null) {
        	electricPowerUsageSummaryService.delete(electricPowerUsageSummary);
        }
    }    		
   
    public void setElectricPowerUsageSummaryService(ElectricPowerUsageSummaryService electricPowerUsageSummaryService) {
        this.electricPowerUsageSummaryService = electricPowerUsageSummaryService;
    }

    public void setRetailCustomerService(RetailCustomerService retailCustomerService) {
        this.retailCustomerService = retailCustomerService;
    }

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }

    public void setExportService(ExportService exportService) {
        this.exportService = exportService;
    }
}
