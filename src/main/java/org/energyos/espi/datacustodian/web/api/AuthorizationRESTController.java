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
import org.energyos.espi.common.domain.Authorization;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.ExportService;
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
public class AuthorizationRESTController {

    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private ExportService exportService;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleGenericException() {}

    // 
    //
    @RequestMapping(value = Routes.AUTHORIZATION_COLLECTION, method = RequestMethod.GET)
	public void index(HttpServletResponse response,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        exportService.exportApplicationInformations(response.getOutputStream(), new ExportFilter(params));
    }

    // 
    //
    @RequestMapping(value = Routes.AUTHORIZATION_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response, 
    		@PathVariable long authorizationId,
    		@RequestParam Map<String, String> params) throws IOException, FeedException {
        exportService.exportApplicationInformation(authorizationId, response.getOutputStream(), new ExportFilter(params));
    }

    // 
    //
    @RequestMapping(value = Routes.AUTHORIZATION_COLLECTION, method = RequestMethod.POST)
    public void create(HttpServletResponse response,
    		@RequestParam Map<String, String> params, 
    		InputStream stream) throws IOException {
        try {
            Authorization authorization = this.authorizationService.importResource(stream);
            authorizationService.add(authorization);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }
    //

    @RequestMapping(value = Routes.AUTHORIZATION_MEMBER, method = RequestMethod.PUT)
    public void update(HttpServletResponse response, 
    		@PathVariable long authorizationId,
    		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException, FeedException {
    	Authorization authorization = authorizationService.findById(authorizationId);
 
        if (authorization != null) {
            try {
            	
                Authorization newAuthorization = authorizationService.importResource(stream);
                authorization.merge(newAuthorization);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    @RequestMapping(value = Routes.AUTHORIZATION_MEMBER, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, 
    		@PathVariable long authorizationId,
    		@RequestParam Map<String, String> params,
    		InputStream stream) throws IOException, FeedException {
    	Authorization authorization = authorizationService.findById(authorizationId);

        if (authorization != null) {
        	authorizationService.delete(authorization);
        }
    }    		
   
    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public void setExportService(ExportService exportService) {
        this.exportService = exportService;
    }
}
