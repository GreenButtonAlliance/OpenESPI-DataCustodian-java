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

package org.energyos.espi.datacustodian.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class DefaultController extends BaseController {

    @RequestMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request, Principal principal) {
        if (request.isUserInRole("ROLE_CUSTODIAN")) {
            return "redirect:/custodian/home";
        } else if (request.isUserInRole("ROLE_USER")) {
            return "redirect:/RetailCustomer/" + currentCustomer(principal).getId() + "/home";
        }
        return "redirect:/home";
    }
}