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

package org.energyos.espi.datacustodian.web.custodian;

import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.service.NotificationService;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class ManagementController extends BaseController {

    
    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = Routes.DATA_CUSTODIAN_NOTIFY_THIRD_PARTY, method = RequestMethod.GET)
    public String notifyThirdParty(@PathVariable Long applicationInformationId, ModelMap model) throws Exception {
    	
		notificationService.notifyAllNeed();

        return "redirect:" + Routes.DATA_CUSTODIAN_HOME;
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_NOTIFY_THIRD_PARTYS, method = RequestMethod.GET)
    public String notifyThirdParty(ModelMap model) throws Exception {
    	
		notificationService.notifyAllNeed();

        return "redirect:" + Routes.DATA_CUSTODIAN_HOME;
    }
    
   public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
   }

   public NotificationService getNotificationService () {
        return this.notificationService;
   }

  
}
