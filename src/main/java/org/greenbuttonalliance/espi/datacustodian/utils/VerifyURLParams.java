/*
 *     Copyright (c) 2018-2019 Green Button Alliance, Inc.
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

package org.greenbuttonalliance.espi.datacustodian.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class VerifyURLParams {

	private static Log logger = LogFactory.getLog(VerifyURLParams.class);

	private VerifyURLParams() {
	}

	public static boolean verifyEntries(String APIUri, Map<String, String> params) {

		if(logger.isInfoEnabled()) {
			logger.info("&n***** VerifyEntries -- Called by " + APIUri + " API Controller *****&n");
		}

		String UTCDateTime = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z";
		String numeric = "\\d+";
		
		boolean validParams = true;

		// Verify "published-max" query parameter is valid if present
		if(params.get("published-max") != null) {

			// Verify published-max only contains numbers
			if(params.get("published-max").matches(UTCDateTime)) {
				} else {
					if(logger.isInfoEnabled()) {
						logger.info("VerifyEntries -- published-max: contains an invalid UTC Datetime value - " +
								params.get("published-max") + "&n");
				}
					validParams = false;
			}
		}
		
		// Verify "published-min" query parameter is valid if present
		if(params.get("published-min") != null) {

			// Verify published-min only contains numbers
			if(params.get("published-min").matches(UTCDateTime)) {
				} else {
					if(logger.isInfoEnabled()) {
						logger.info("VerifyEntries -- published-min: contains an invalid UTC Datetime value - " +
								params.get("published-min") + "&n");
					}
					validParams = false;
			}
		}
		
		// Verify "updated-max" query parameter is valid if present
		if(params.get("updated-max") != null) {

			// Verify updated-max only contains numbers
			if(params.get("updated-max").matches(UTCDateTime)) {
				} else {
					if(logger.isInfoEnabled()) {
						logger.info("VerifyEntries -- updated-max: contains an invalid UTC Datetime value - " +
								params.get("updated-max") + "&n");
					}
					validParams = false;
			}
		}
		
		// Verify "updated-min" query parameter is valid if present
		if(params.get("updated-min") != null) {

			// Verify updated-min only contains numbers
			if(params.get("updated-min").matches(UTCDateTime)) {
				} else {
					if(logger.isInfoEnabled()) {
						logger.info("VerifyEntries -- updated-min: contains an invalid UTC Datetime value - " +
								params.get("updated-min") + "&n");
					}
					validParams = false;
			}
		}
		
		// Verify "max-results" query parameter is valid if present
		if(params.get("max-results") != null) {

			// Verify max-results only contains numbers
			if(params.get("max-results").matches(numeric)) {
				} else {
					if(logger.isInfoEnabled()) {
						logger.info("VerifyEntries -- max-results: contains an invalid numeric value - " +
								params.get("max-results") + "&n");
					}
					validParams = false;
			}
		}
		
		// Verify "start-index" query parameter is valid if present
		if(params.get("start-index") != null) {

			// Verify start-index only contains numbers
			if(params.get("start-index").matches(numeric)) {
				} else {
					if(logger.isInfoEnabled()) {
						logger.info("VerifyEntries -- start-index: contains an invalid numeric value - " +
								params.get("start-index") + "&n");
					}
					validParams = false;
			}
		}
		
		// Verify "depth" query parameter is valid if present
		if(params.get("depth") != null) {

			// Verify depth only contains numbers
			if(params.get("depth").matches(numeric)) {
				} else {
					if(logger.isInfoEnabled()) {
						logger.info("VerifyEntries -- depth: contains an invalid numeric value - " +
								params.get("depth") + "&n");
					}
					validParams = false;
			}
		}
		
		return validParams;
		
	}

}
