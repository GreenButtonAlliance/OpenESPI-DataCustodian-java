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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.energyos.espi.common.domain.ApplicationInformation;
import org.energyos.espi.common.service.ApplicationInformationService;
import org.energyos.espi.common.test.EspiFactory;

import org.junit.Test;

public class ScopeSelectionControllerTests {

    @Test
    public void scopeSelection() throws Exception {
        ScopeSelectionController controller = new ScopeSelectionController();

        ApplicationInformation applicationInformation = EspiFactory.newApplicationInformation();
        applicationInformation.setThirdPartyScopeSelectionScreenURI("http://localhost:8080/ThirdParty/RetailCustomer/ScopeSelection");

        ApplicationInformationService applicationInformationService = mock(ApplicationInformationService.class);
        controller.setApplicationInformationService(applicationInformationService);
        when(applicationInformationService.findByClientId(applicationInformation.getClientId())).thenReturn(applicationInformation);

        String redirectURL = controller.scopeSelection(null, new String[]{"scope1", "scope2"}, applicationInformation.getClientId());
        String[] scopes = applicationInformation.getScope().toArray(new String[applicationInformation.getScope().size()]);

        assertEquals(String.format("redirect:%s?scope=%s&scope=%s&DataCustodianID=%s", applicationInformation.getThirdPartyScopeSelectionScreenURI(),
                scopes[0], scopes[1], applicationInformation.getDataCustodianId()),
                redirectURL);
    }
}
