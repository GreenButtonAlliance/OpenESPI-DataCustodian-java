package org.energyos.espi.datacustodian.web.custodian;

import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.NotificationService;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class AdminController extends BaseController {
  
    @Autowired
    private SubscriptionService subscriptionService;
    
    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = Routes.DATA_CUSTODIAN_NOTIFY_THIRD_PARTY)
    public String notifyThirdParty() throws Exception {
        for(Subscription subscription : subscriptionService.findAll()) {
           notificationService.notify(subscription, null, null);
        }
        return "redirect:" + Routes.DATA_CUSTODIAN_HOME;
    }   
}
