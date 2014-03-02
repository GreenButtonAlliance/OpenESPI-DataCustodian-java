package org.energyos.espi.datacustodian.web.custodian;

import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.service.NotificationService;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class NotifyThirdParty extends BaseController {

    
    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = Routes.DATA_CUSTODIAN_NOTIFY_THIRD_PARTY, method = RequestMethod.GET)
    public String notifyThirdParty() throws Exception {
    	
		notificationService.notifyAllNeed();


        return "redirect:" + Routes.DATA_CUSTODIAN_HOME;
    }

  
}
