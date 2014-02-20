package org.energyos.espi.datacustodian.web.api;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.energyos.espi.common.domain.ReadingType;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ReadingTypeService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.utils.ExportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sun.syndication.io.FeedException;

@Controller
public class ReadingTypeRESTController {

    @Autowired
    private ReadingTypeService readingTypeService;
    
    @Autowired
    private ResourceService resourceService;
    
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

    @RequestMapping(value = Routes.ROOT_READING_TYPE_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response,
    		@PathVariable long readingTypeId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportReadingType(readingTypeId, response.getOutputStream(), new ExportFilter(params));
    }

    @RequestMapping(value = Routes.ROOT_READING_TYPE_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response, 
    		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        try {
            ReadingType readingType = this.readingTypeService.importResource(stream);
            exportService.exportReadingType(readingType.getId(), response.getOutputStream(), new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @RequestMapping(value = Routes.ROOT_READING_TYPE_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response, 
    		@PathVariable long readingTypeId,
    		@RequestParam Map<String, String> params,
    	    InputStream stream) throws IOException {
        ReadingType existingReadingType = readingTypeService.findById(readingTypeId);
        
        if (existingReadingType != null) {
            try {
               // importing the resource will perform a merge (or replace) if needed
               // an exception will be thrown if it is an invalid readingTypeId
               readingTypeService.importResource(stream);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @RequestMapping(value = Routes.ROOT_READING_TYPE_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, 
    		@PathVariable long readingTypeId,
    		@RequestParam Map<String, String> params) throws IOException {
        try { 
        	   resourceService.deleteById(readingTypeId, ReadingType.class);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);  
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

    @RequestMapping(value = Routes.READING_TYPE_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long meterReadingId,
    		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);

        try {
            ReadingType readingType = readingTypeService.importResource(stream);
            // TODO - uncomment after we decouple importResource from New-Or-Merge
            //        readingTypeService.associateByUUID(meterReading, readingType.getUUID());
            exportService.exportReadingType(retailCustomerId, usagePointId, meterReadingId, readingType.getId(), response.getOutputStream(), new ExportFilter(params));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @RequestMapping(value = Routes.READING_TYPE_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response, 
    		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long meterReadingId,
    		@PathVariable long readingTypeId,
    		@RequestParam Map<String, String> params,
    	    InputStream stream) throws IOException {

        try {
            readingTypeService.importResource(stream);
        } catch (Exception e) {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @RequestMapping(value = Routes.READING_TYPE_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, 
       		@PathVariable long retailCustomerId,
    		@PathVariable long usagePointId,
    		@PathVariable long meterReadingId,
    		@PathVariable long readingTypeId,
    		@RequestParam Map<String, String> params) throws IOException {

        try {
            resourceService.deleteByXPathId(retailCustomerId, usagePointId, meterReadingId, readingTypeId, ReadingType.class); 

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } 

    }

    public void setReadingTypeService(ReadingTypeService readingTypeService) {
        this.readingTypeService = readingTypeService;
    }


    public void setExportService(ExportService exportService) {
        this.exportService = exportService;
    }
    
    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
    
}
