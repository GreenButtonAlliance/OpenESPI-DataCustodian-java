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
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.domain.MeterReading;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.ReadingType;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.MeterReadingService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.ReadingTypeService;
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
public class ReadingTypeRESTController {

    @Autowired
    private RetailCustomerService retailCustomerService;
    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private MeterReadingService meterReadingService;
    @Autowired
    private ReadingTypeService readingTypeService;
    
    @Autowired
    private ExportService exportService;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleGenericException() {}

    // ROOT RESTful Forms
    //
    @RequestMapping(value = Routes.ROOT_READING_TYPE_COLLECTION, method = RequestMethod.GET)
    public void index(HttpServletResponse response, 
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportReadingTypes(response.getOutputStream(), new ExportFilter(params));
    }

    // 
    //
    @RequestMapping(value = Routes.ROOT_READING_TYPE_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response,
    		@PathVariable long readingTypeId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportReadingType(readingTypeId, response.getOutputStream(), new ExportFilter(params));
    }

    // 
    //
    @RequestMapping(value = Routes.ROOT_READING_TYPE_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response, 
    		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException {
        try {
            ReadingType readingType = this.readingTypeService.importResource(stream);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    //
    @RequestMapping(value = Routes.ROOT_READING_TYPE_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response, 
    		@PathVariable long readingTypeId,
    		@RequestParam Map<String, String> params,
    	    InputStream stream) throws IOException {
        ReadingType existingReadingType = readingTypeService.findById(readingTypeId);
        
        if (existingReadingType != null) {
            try {
                ReadingType readingType = readingTypeService.importResource(stream);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    @RequestMapping(value = Routes.ROOT_READING_TYPE_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, 
    		@PathVariable long readingTypeId,
    		@RequestParam Map<String, String> params) throws IOException {
        ReadingType readingType = readingTypeService.findById(readingTypeId);

        if (readingType != null) {
            this.readingTypeService.deleteById(readingTypeId);
        }
    }

    // xpath RESTful forms
    //
    @RequestMapping(value = Routes.READING_TYPE_COLLECTION, method = RequestMethod.GET)
    public void index(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long meterReadingId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportReadingTypes(retailCustomerId, usagePointId, response.getOutputStream(), new ExportFilter(params));
    }

    // 
    //
    @RequestMapping(value = Routes.READING_TYPE_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response,
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long meterReadingId,
    		@PathVariable long readingTypeId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportReadingType(retailCustomerId, usagePointId, meterReadingId, readingTypeId, response.getOutputStream(), new ExportFilter(params));
    }

    // 
    //
    @RequestMapping(value = Routes.READING_TYPE_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long meterReadingId,
    		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        UsagePoint usagePoint = usagePointService.findById(usagePointId);
        MeterReading meterReading = meterReadingService.findById(retailCustomerId, usagePointId, meterReadingId);
        try {
            ReadingType readingType = this.readingTypeService.importResource(stream);
            readingTypeService.associateByUUID(meterReading, readingType.getUUID());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    //
    @RequestMapping(value = Routes.READING_TYPE_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long meterReadingId,
    		@PathVariable long readingTypeId,
    		@RequestParam Map<String, String> params,
    	    InputStream stream) throws IOException {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        UsagePoint usagePoint = usagePointService.findById(usagePointId);
        MeterReading meterReading = meterReadingService.findById(retailCustomerId, usagePointId, meterReadingId);
        ReadingType existingReadingType = readingTypeService.findById(readingTypeId);
        
        if (existingReadingType != null) {
            try {
                ReadingType readingType = readingTypeService.importResource(stream);
                readingTypeService.associateByUUID(meterReading, readingType.getUUID());
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    @RequestMapping(value = Routes.READING_TYPE_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, 
       		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long meterReadingId,
    		@PathVariable long readingTypeId,
    		@RequestParam Map<String, String> params) throws IOException {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        UsagePoint usagePoint = usagePointService.findById(usagePointId);
        MeterReading meterReading = meterReadingService.findById(retailCustomerId, usagePointId, meterReadingId);
        ReadingType readingType = readingTypeService.findById(readingTypeId);

        

        if (readingType != null) {
            this.readingTypeService.deleteById(readingTypeId);
        }
    }

    public void setReadingTypeService(ReadingTypeService readingTypeService) {
        this.readingTypeService = readingTypeService;
    }

    public void setRetailCustomerService(RetailCustomerService retailCustomerService) {
        this.retailCustomerService = retailCustomerService;
    }

    public void setExportService(ExportService exportService) {
        this.exportService = exportService;
    }
}
