package org.energyos.espi.datacustodian.web.customer;

import org.energyos.espi.common.domain.Configuration;
import org.energyos.espi.common.domain.ThirdParty;
import org.energyos.espi.common.service.ThirdPartyService;
import org.energyos.espi.datacustodian.utils.URLHelper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ThirdPartyControllerTest {
    private ThirdPartyController controller;
    private ModelMap model;
    private ThirdPartyService thirdPartyService;

    @Before
    public void before() {
        controller = new ThirdPartyController();
        thirdPartyService = mock(ThirdPartyService.class);
        controller.setThirdPartyService(thirdPartyService);
        model = new ModelMap();
    }

    @Test
    public void index_displaysIndexView() {
        assertEquals("/customer/thirdparties/index", controller.index(model));
    }

    @Test
    public void index_setsThirdPartiesModel() {
        List<ThirdParty> thirdParties = new ArrayList<>();

        when(thirdPartyService.findAll()).thenReturn(thirdParties);

        controller.index(model);

        assertEquals(thirdParties, model.get("thirdParties"));
        verify(thirdPartyService).findAll();
    }

    @Test
    public void selectThirdParty_redirectsToThirdPartyUrl() {
        String Third_party_URL = "http://example.com";
        String redirectUrl = "redirect:" + Third_party_URL + "?" + URLHelper.newScopeParams(Configuration.SCOPES) + "&DataCustodianID=" + Configuration.DATA_CUSTODIAN_ID;

        assertEquals(redirectUrl, controller.selectThirdParty(Third_party_URL));
    }
}
