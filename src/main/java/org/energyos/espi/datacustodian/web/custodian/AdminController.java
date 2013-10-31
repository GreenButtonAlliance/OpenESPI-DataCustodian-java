package org.energyos.espi.datacustodian.web.custodian;

import org.energyos.espi.datacustodian.domain.Routes;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.service.NotificationService;
import org.energyos.espi.datacustodian.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class AdminController {

    @Autowired
    private ConsumerTokenServices tokenServices;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = Routes.DataCustodianNotifyThirdParty, method = RequestMethod.GET)
    public String notifyThirdParty() throws Exception {
        for(Subscription subscription : subscriptionService.findAll()) {
           notificationService.notify(subscription);
        }

        return "redirect:" + Routes.DataCustodianHome;
    }

    @RequestMapping(value = Routes.DataCustodianRemoveAllOAuthTokens, method = RequestMethod.GET)
    public String revokeToken() throws Exception {

        for(OAuth2AccessToken t: tokenServices.findTokensByClientId("third_party")) {
            tokenServices.revokeToken(t.getValue());
        }

        return "redirect:" + Routes.DataCustodianHome;
    }
}
