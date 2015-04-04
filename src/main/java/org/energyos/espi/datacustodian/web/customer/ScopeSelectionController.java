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

import static org.energyos.espi.datacustodian.utils.URLHelper.newScopeParams;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.energyos.espi.common.domain.ApplicationInformation;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.service.ApplicationInformationService;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
public class ScopeSelectionController extends BaseController {

	@Autowired
	private ApplicationInformationService applicationInformationService;

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Access Not Authorized")
	public void handleGenericException() {
	}

	@RequestMapping(value = Routes.DATA_CUSTODIAN_SCOPE_SELECTION_SCREEN, method = RequestMethod.GET)
	public String scopeSelection(HttpServletRequest request, String[] scopes,
			@RequestParam("ThirdPartyID") String thirdPartyClientId)
			throws Exception {

		try {
			ApplicationInformation applicationInformation = applicationInformationService
					.findByClientId(thirdPartyClientId);

			return "redirect:"
					+ applicationInformation
							.getThirdPartyScopeSelectionScreenURI() + "?"
					+ newScopeParams(applicationInformation.getScope())
					+ "&DataCustodianID="
					+ applicationInformation.getDataCustodianId();
		} catch (NoResultException | EmptyResultDataAccessException e) {
			System.out.printf(
					"ScopeSelectionController: ApplicationInformation record not found!  "
							+ "ThirdPartyID = %s\n", thirdPartyClientId);
			throw new Exception("Access Not Authorized");
		}
	}

	public void setApplicationInformationService(
			ApplicationInformationService applicationInformationService) {
		this.applicationInformationService = applicationInformationService;
	}
}