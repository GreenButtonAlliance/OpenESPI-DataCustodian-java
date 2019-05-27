/*
 *     Copyright (c) 2018 Green Button Alliance, Inc.
 *
 *     Portions copyright (c) 2013-2018 EnergyOS.org
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.greenbuttonalliance.espi.datacustodian.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.greenbuttonalliance.espi.common.domain.RetailCustomer;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

public class BaseController {

	Log logger = LogFactory.getLog(BaseController.class);

	@ModelAttribute("currentCustomer")
	public RetailCustomer currentCustomer(Principal principal) {
		try {
			if(logger.isInfoEnabled()) {
				logger.info("BaseController: currentCustomer -- " +
						(RetailCustomer) ((Authentication) principal).getPrincipal());
			}
			return (RetailCustomer) ((Authentication) principal).getPrincipal();
		} catch (Exception e) {
			if(logger.isErrorEnabled()) {
				logger.error("BaseController: currentCustomer -- null&n");
			}
			return null;
		}
	}
}
