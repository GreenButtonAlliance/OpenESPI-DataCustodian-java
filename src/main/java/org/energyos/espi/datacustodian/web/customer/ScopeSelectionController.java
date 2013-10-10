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

import org.energyos.espi.datacustodian.domain.Configuration;
import org.energyos.espi.datacustodian.domain.ThirdParty;
import org.energyos.espi.datacustodian.service.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static org.energyos.espi.datacustodian.utils.URLHelper.newScopeParams;

@Controller
@RequestMapping("/RetailCustomer")
@PreAuthorize("hasRole('ROLE_USER')")
public class ScopeSelectionController {

    @Autowired
    private ThirdPartyService thirdPartyService;

    @RequestMapping(value = "/ScopeSelectionList", method = RequestMethod.GET)
    public String scopeSelection(String[] scopes, @RequestParam("ThirdPartyID") String thirdPartyClientId) {
        ThirdParty thirdParty = thirdPartyService.findByClientId(thirdPartyClientId);
        return "redirect:" + thirdParty.getUrl() + "?" + newScopeParams(Configuration.SCOPES) +
                "&DataCustodianID=" + Configuration.DATA_CUSTODIAN_ID;
    }

    public void setThirdPartyService(ThirdPartyService thirdPartyService) {
        this.thirdPartyService = thirdPartyService;
    }

}