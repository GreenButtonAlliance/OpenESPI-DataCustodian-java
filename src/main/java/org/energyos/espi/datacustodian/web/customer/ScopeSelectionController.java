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

package org.energyos.espi.datacustodian.web.customer;

import static org.energyos.espi.datacustodian.utils.URLHelper.newScopeParams;

import org.energyos.espi.common.domain.ApplicationInformation;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.service.ApplicationInformationService;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

///DataCustodian/src/main/java/org/energyos/espi/datacustodian/utils/URLHelper.java

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
public class ScopeSelectionController extends BaseController {

    @Autowired
    private ApplicationInformationService applicationInformationService;

    @RequestMapping(value = Routes.DATA_CUSTODIAN_SCOPE_SELECTION_SCREEN, method = RequestMethod.GET)
    public String scopeSelection(String[] scopes, @RequestParam("ThirdPartyID") String thirdPartyClientId) {
        ApplicationInformation applicationInformation = applicationInformationService.findByClientId(thirdPartyClientId);
        return "redirect:" + 
        		applicationInformation.getThirdPartyScopeSelectionScreenURI() + 
        		"?" + 
        		newScopeParams(applicationInformation.getScope()) +
                "&DataCustodianID=" + applicationInformation.getDataCustodianId();
    }


	public void setApplicationInformationService(ApplicationInformationService applicationInformationService) {
        this.applicationInformationService = applicationInformationService;
    }
}