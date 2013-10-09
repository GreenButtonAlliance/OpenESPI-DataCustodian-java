package org.energyos.espi.datacustodian.web.custodian;

import org.energyos.espi.datacustodian.domain.*;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.*;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Collection;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class AdminController {

    @Autowired
    private ConsumerTokenServices tokenServices;

    @RequestMapping(value = Routes.DataCustodianRemoveAllOAuthTokens, method = RequestMethod.GET)
    public String revokeToken() throws Exception {

        for(OAuth2AccessToken t: tokenServices.findTokensByClientId("third_party")) {
            tokenServices.revokeToken(t.getValue());
        }

        return "redirect:" + Routes.DataCustodianHome;
    }
}
