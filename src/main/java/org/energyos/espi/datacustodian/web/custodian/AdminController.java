package org.energyos.espi.datacustodian.web.custodian;

import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.datacustodian.service.NotificationService;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class AdminController extends BaseController {

    @Autowired
    private ConsumerTokenServices tokenServices;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = Routes.DATA_CUSTODIAN_NOTIFY_THIRD_PARTY, method = RequestMethod.GET)
    public String notifyThirdParty() throws Exception {
        for(Subscription subscription : subscriptionService.findAll()) {
           notificationService.notify(subscription, null, null);
        }

        return "redirect:" + Routes.DATA_CUSTODIAN_HOME;
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_REMOVE_ALL_OAUTH_TOKENS, method = RequestMethod.GET)
    public String revokeToken() throws Exception {

        for(OAuth2AccessToken t: tokenServices.findTokensByClientId("third_party")) {
            tokenServices.revokeToken(t.getValue());
        }

        return "redirect:" + Routes.DATA_CUSTODIAN_HOME;
    }
}
