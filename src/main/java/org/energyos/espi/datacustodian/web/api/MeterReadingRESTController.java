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

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.domain.MeterReading;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.MeterReadingService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.utils.EntryTypeIterator;
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
public class MeterReadingRESTController {

    @Autowired
    private MeterReadingService meterReadingService;
    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private RetailCustomerService retailCustomerService;
    @Autowired
    private ExportService exportService;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleGenericException() {}

    // ROOT RESTFul APIs
    //
    @RequestMapping(value = Routes.ROOT_METER_READING_COLLECTION, method = RequestMethod.GET)
    public void index(HttpServletResponse response, @RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportMeterReadings(response.getOutputStream(), new ExportFilter(params));
    }

    @RequestMapping(value = Routes.ROOT_METER_READING_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response, 
		     @PathVariable long meterReadingId,
		     @RequestParam Map<String, String> params)
        throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        try {
            exportService.exportMeterReading(meterReadingId, response.getOutputStream(), new ExportFilter(params));   
                    } catch (Exception e) {
              response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
          } 
    }

    @RequestMapping(value = Routes.ROOT_METER_READING_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response, 
		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException {
        try {
            MeterReading meterReading = this.meterReadingService.importResource(stream);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @RequestMapping(value = Routes.ROOT_METER_READING_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response,
		       @PathVariable long meterReadingId,
		       @RequestParam Map<String, String> params,
		       InputStream stream) {
        MeterReading meterReading = meterReadingService.findById(meterReadingId);
        if (meterReading != null) {
            try {
                MeterReading newMeterReading = meterReadingService.importResource(stream);
		meterReading.merge(newMeterReading);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    @RequestMapping(value = Routes.ROOT_METER_READING_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, 
    		@PathVariable long meterReadingId    		
    		) {
        MeterReading meterReading = meterReadingService.findById(meterReadingId);
       
        if (meterReading != null) {
            this.meterReadingService.delete(meterReading);
        }
    }

    // XPath RESTFul APIs
    //
    @RequestMapping(value = Routes.METER_READING_COLLECTION, method = RequestMethod.GET)
    public void index(HttpServletResponse response, 
    		@PathVariable long retailCustomerId, 
    		@PathVariable long usagePointId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportMeterReadings(retailCustomerId, usagePointId, response.getOutputStream(), new ExportFilter(params));
    }

    @RequestMapping(value = Routes.METER_READING_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response, 
		     @PathVariable long retailCustomerId,
		     @PathVariable long usagePointId,
		     @PathVariable long meterReadingId,
		     @RequestParam Map<String, String> params)
        throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        try {
            exportService.exportMeterReading(retailCustomerId, usagePointId, meterReadingId, response.getOutputStream(), new ExportFilter(params));
                    } catch (Exception e) {
              response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
          } 
    }

    // 
    //
    @RequestMapping(value = Routes.METER_READING_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException {
              UsagePoint usagePoint = usagePointService.findById(new Long(retailCustomerId), new Long(usagePointId));
        try {
            MeterReading meterReading = this.meterReadingService.importResource(stream);
            meterReadingService.associateByUUID(usagePoint, meterReading.getUUID());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    //
    @RequestMapping(value = Routes.METER_READING_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response,
		       @PathVariable long retailCustomerId,
		       @PathVariable long usagePointId,
		       @PathVariable long meterReadingId,
		       @RequestParam Map<String, String> params,
		       InputStream stream) {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        UsagePoint usagePoint = usagePointService.findById(usagePointId);
        MeterReading meterReading = meterReadingService.findById(retailCustomerId, usagePointId, meterReadingId);
       
        if (meterReading != null) {
            try {
                MeterReading newmeterReading = meterReadingService.importResource(stream);
                meterReadingService.associateByUUID(usagePoint, newmeterReading.getUUID());
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    @RequestMapping(value = Routes.METER_READING_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long meterReadingId    		
    		) {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        UsagePoint usagePoint = usagePointService.findById(usagePointId);
        MeterReading meterReading = meterReadingService.findById(retailCustomerId, usagePointId, meterReadingId);
       
        if (meterReading != null) {
            this.meterReadingService.delete(meterReading);
        }
    }

    public void setMeterReadingService(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    public void setRetailCustomerService(RetailCustomerService retailCustomerService) {
        this.retailCustomerService = retailCustomerService;
    }

    public void setAtomService(MeterReadingService atomService) {
        this.meterReadingService = atomService;
    }
    
    public void setExportService(ExportService exportService) {
    	this.exportService = exportService;
    }

}
