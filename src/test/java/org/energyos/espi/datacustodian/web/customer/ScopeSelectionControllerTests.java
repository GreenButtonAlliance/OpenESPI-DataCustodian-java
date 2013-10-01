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
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScopeSelectionControllerTests {

    @Test
    public void scopeSelection() throws Exception {
        ScopeSelectionController controller = new ScopeSelectionController();

        ThirdParty thirdParty = EspiFactory.newThirdParty();
        thirdParty.setUrl("http://localhost:8080/ThirdParty/RetailCustomer/ScopeSelection");

        ThirdPartyService thirdPartyService = mock(ThirdPartyService.class);
        controller.setThirdPartyService(thirdPartyService);
        when(thirdPartyService.findByClientId(thirdParty.getClientId())).thenReturn(thirdParty);

        String redirectURL = controller.scopeSelection(new String[]{"scope1", "scope2"}, thirdParty.getClientId());

        assertEquals(String.format("redirect:%s?scope=%s&scope=%s&scope=%s&DataCustodianID=%s", thirdParty.getUrl(),
                Configuration.SCOPES[0], Configuration.SCOPES[1], Configuration.SCOPES[2], Configuration.DATA_CUSTODIAN_ID),
                redirectURL);
    }
}
