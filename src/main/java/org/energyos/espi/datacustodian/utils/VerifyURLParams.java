/*
 * Copyright 2017 REMI Networks
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

package org.energyos.espi.datacustodian.utils;

import java.util.Map;

/*
 * Copyright 2017 REMI Networks
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

public class VerifyURLParams {
	
	public static boolean verifyEntries(String APIUri, Map<String, String> params) {
		
		System.out
		.printf("\n***** VerifyEntries -- Called by %s API Controller *****\n", APIUri);
		
		String UTCDateTime = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z";
		String numeric = "\\d+";
		
		boolean validParams = true;

		// Verify "published-max" query parameter is valid if present
		if(params.get("published-max") != null) {

			// Verify published-max only contains numbers
			if(params.get("published-max").matches(UTCDateTime)) {
				} else {
					System.out
					.printf("VerifyEntries -- published-max: contains an invalid UTC Datetime value - %s\n",
							params.get("published-max"));
					validParams = false;
			}
		}
		
		// Verify "published-min" query parameter is valid if present
		if(params.get("published-min") != null) {

			// Verify published-min only contains numbers
			if(params.get("published-min").matches(UTCDateTime)) {
				} else {
					System.out
					.printf("VerifyEntries -- published-min: contains an invalid UTC Datetime value - %s\n",
							params.get("published-min"));
					validParams = false;
			}
		}
		
		// Verify "updated-max" query parameter is valid if present
		if(params.get("updated-max") != null) {

			// Verify updated-max only contains numbers
			if(params.get("updated-max").matches(UTCDateTime)) {
				} else {
					System.out
					.printf("VerifyEntries -- updated-max: contains an invalid UTC Datetime value - %s\n",
							params.get("updated-max"));
					validParams = false;
			}
		}
		
		// Verify "updated-min" query parameter is valid if present
		if(params.get("updated-min") != null) {

			// Verify updated-min only contains numbers
			if(params.get("updated-min").matches(UTCDateTime)) {
				} else {
					System.out
					.printf("VerifyEntries -- updated-min: contains an invalid UTC Datetime value - %s\n",
							params.get("updated-min"));
					validParams = false;
			}
		}
		
		// Verify "max-results" query parameter is valid if present
		if(params.get("max-results") != null) {

			// Verify max-results only contains numbers
			if(params.get("max-results").matches(numeric)) {
				} else {
					System.out
					.printf("VerifyEntries -- max-results: contains an invalid numeric value - %s\n",
							params.get("max-results"));
					validParams = false;
			}
		}
		
		// Verify "start-index" query parameter is valid if present
		if(params.get("start-index") != null) {

			// Verify start-index only contains numbers
			if(params.get("start-index").matches(numeric)) {
				} else {
					System.out
					.printf("VerifyEntries -- start-index: contains an invalid numeric value - %s\n",
							params.get("start-index"));
					validParams = false;
			}
		}
		
		// Verify "depth" query parameter is valid if present
		if(params.get("depth") != null) {

			// Verify depth only contains numbers
			if(params.get("depth").matches(numeric)) {
				} else {
					System.out
					.printf("VerifyEntries -- depth: contains an invalid numeric value - %s\n",
							params.get("depth"));
					validParams = false;
			}
		}
		
		return validParams;
		
	}

}
