/*
 * Copyright 2013, 2014, 2015 EnergyOS.org
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

import org.energyos.espi.common.domain.ApplicationInformation;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.service.ApplicationInformationService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.datacustodian.utils.URLHelper;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(Routes.THIRD_PARTY_LIST)
public class ThirdPartyController extends BaseController {

	@Autowired
	private ApplicationInformationService applicationInformationService;

	@Autowired
	private ResourceService resourceService;

	@RequestMapping(method = RequestMethod.GET)
	public String index(ModelMap model) {
		// note that we are only looking at "THIRD_PARTY" relationships here.
		model.put("applicationInformationList",
				applicationInformationService.findByKind("THIRD_PARTY"));
		return "/customer/thirdparties/index";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String selectThirdParty(
			@RequestParam("Third_party") Long thirdPartyId,
			@RequestParam("Third_party_URL") String thirdPartyURL) {
		ApplicationInformation applicationInformation = resourceService
				.findById(thirdPartyId, ApplicationInformation.class);
		return "redirect:" + thirdPartyURL + "?"
				+ URLHelper.newScopeParams(applicationInformation.getScope())
				+ "&DataCustodianID="
				+ applicationInformation.getDataCustodianId();
	}

	public void setApplicationInformationService(
			ApplicationInformationService applicationInformationService) {
		this.applicationInformationService = applicationInformationService;
	}

	public ApplicationInformationService gettApplicationInformationService(
			ApplicationInformationService applicationInformationService) {
		return this.applicationInformationService;
	}

}
