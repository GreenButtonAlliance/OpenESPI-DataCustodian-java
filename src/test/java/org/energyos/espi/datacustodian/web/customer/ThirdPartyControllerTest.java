package org.energyos.espi.datacustodian.web.customer;

import org.energyos.espi.common.domain.ApplicationInformation;
import org.energyos.espi.common.domain.Configuration;
import org.energyos.espi.common.service.ApplicationInformationService;
import org.energyos.espi.common.test.EspiFactory;
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
    private ApplicationInformationService applicationInformationService;

    @Before
    public void before() {
        controller = new ThirdPartyController();
        applicationInformationService = mock(ApplicationInformationService.class);
        controller.setApplicationInformationService(applicationInformationService);
        model = new ModelMap();
    }

    @Test
    public void index_displaysIndexView() {
        assertEquals("/customer/thirdparties/index", controller.index(model));
    }

    @Test
    public void index_setsThirdPartiesModel() {
        List<ApplicationInformation> applicationInformationList = new ArrayList<>();

        when(applicationInformationService.findAll()).thenReturn(applicationInformationList);

        controller.index(model);

        assertEquals(applicationInformationList, model.get("applicationInformationList"));
        verify(applicationInformationService).findAll();
    }

    @Test
    public void selectThirdParty_redirectsToThirdPartyUrl() {
        ApplicationInformation applicationInformation = EspiFactory.newApplicationInformation();

        String redirectUrl = "redirect:" + applicationInformation.getRedirectUri() + "?" +
                URLHelper.newScopeParams(applicationInformation.getScope()) + "&DataCustodianID=" + Configuration.DATA_CUSTODIAN_ID;

        when(applicationInformationService.findById(anyLong())).thenReturn(applicationInformation);

        assertEquals(redirectUrl, controller.selectThirdParty(1L, applicationInformation.getRedirectUri()));
    }
}
