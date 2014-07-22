/* Copyright 2013, 2014 EnergyOS.org
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


/**
 * 
 */
package org.energyos.espi.datacustodian.web.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.energyos.espi.common.domain.Routes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A Controller that supports administrative management capabilities within the
 * data custodian
 * 
 * @author jat1
 *
 */
@Controller
public class ManageRESTController {

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleGenericException() {
	}

	/**
	 * Provides access to administrative commands through the pattern:
	 *    DataCustodian/manage?command=[resetDataCustodianDB | initializeDataCustodianDB]
	 *    
	 * @param response Contains text version of stdout of the command
	 * @param params [["command" . ["resetDataCustodianDB" | "initializeDataCustodianDB"]]]
	 * @param stream 
	 * @throws IOException
	 */
	@RequestMapping(value = Routes.DATA_CUSTODIAN_MANAGE, method = RequestMethod.GET, produces = "text/text")
	@ResponseBody
	public void doCommand(HttpServletResponse response,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException {

		try {
			try {
				String commandString = params.get("command");
				System.out.println("[Manage] " + commandString);
				ServletOutputStream output = response.getOutputStream();
				
				output.println("[Manage] Restricted Management Interface");
				output.println("[Manage] Request: " + commandString);
				
				String command=null;

				// parse command
				if(commandString.contains("resetDataCustodianDB")){
					command="/etc/OpenESPI/DataCustodian/resetDatabase.sh";
				}
				else if (commandString.contains("initializeDataCustodianDB")){
					command="/etc/OpenESPI/DataCustodian/initializeDatabase.sh";
		
				}

				if(command != null) {
					Process p = Runtime.getRuntime().exec(command);
					p.waitFor();
					output.println("[Manage] Result: ");
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					
					String line = reader.readLine();
	
					while (line != null) {
						System.out.println("[Manage] " + line);
						output.println("[Manage]: " + line);
						line = reader.readLine();
					}
					reader = new BufferedReader(
							new InputStreamReader(p.getErrorStream()));
					output.println("[Manage] Errors: ");
					line = reader.readLine();
					while (line != null) {
						System.out.println("[Manage] " + line);
						output.println("[Manage]: " + line);
						line = reader.readLine();
					}					
				}


			} catch (IOException e1) {
			} catch (InterruptedException e2) {
			}

			System.out.println("[Manage] " + "Done");

		} catch (Exception e) {
			System.out.printf("**** [Manage] Error: %s\n", e.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

}
