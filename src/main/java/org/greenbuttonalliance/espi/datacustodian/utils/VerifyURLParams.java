/*
 *    Copyright (c) 2018-2020 Green Button Alliance, Inc.
 *
 *    Portions copyright (c) 2013-2018 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.greenbuttonalliance.espi.datacustodian.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class VerifyURLParams {

	private static final String PUBLISHED_MAX = "published-max";

	private static final String PUBLISHED_MIN = "published-min";

	private static final String UPDATED_MAX = "updated-max";

	private static final String UPDATED_MIN = "updated-min";

	private static final String MAX_RESULTS = "max-results";

	private static final String START_INDEX = "start-index";

	private static final String DEPTH = "depth";

	private static boolean validParams;

	private static Log logger = LogFactory.getLog(VerifyURLParams.class);

	private VerifyURLParams() {
	}

	public static boolean verifyEntries(String APIUri, Map<String, String> params) {

		if(logger.isInfoEnabled()) {
			logger.info("&n***** VerifyEntries -- Called by " + APIUri + " API Controller *****&n");
		}

		String UTCDateTime = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z";
		String numeric = "\\d+";

        // Verify "published-max" query parameter is valid if present
		if(params.get(PUBLISHED_MAX) != null) {

			// Verify published-max only contains numbers
			if(params.get(PUBLISHED_MAX).matches(UTCDateTime)) validParams = true;

            else {
				if(logger.isInfoEnabled()) {
					logger.info("VerifyEntries -- published-max: contains an invalid UTC Datetime value - " +
							params.get(PUBLISHED_MAX) + "&n");
				}

				validParams = false;
			}
		}
		
		// Verify "published-min" query parameter is valid if present
		if(params.get(PUBLISHED_MIN) != null) {

			// Verify published-min only contains numbers
			if(params.get(PUBLISHED_MIN).matches(UTCDateTime)) validParams = true;

            else {
				if(logger.isInfoEnabled()) {
					logger.info("VerifyEntries -- published-min: contains an invalid UTC Datetime value - " +
							params.get(PUBLISHED_MIN) + "&n");
				}

				validParams = false;
			}
		}
		
		// Verify "updated-max" query parameter is valid if present
		if(params.get(UPDATED_MAX) != null) {

			// Verify updated-max only contains numbers
			if(params.get(UPDATED_MAX).matches(UTCDateTime)) validParams = true;

            else {
				if(logger.isInfoEnabled()) {
					logger.info("VerifyEntries -- updated-max: contains an invalid UTC Datetime value - " +
							params.get(UPDATED_MAX) + "&n");
				}

				validParams = false;
			}
		}
		
		// Verify "updated-min" query parameter is valid if present
		if(params.get(UPDATED_MIN) != null) {

			// Verify updated-min only contains numbers
			if(params.get(UPDATED_MIN).matches(UTCDateTime)) validParams = true;

            else {
				if(logger.isInfoEnabled()) {
					logger.info("VerifyEntries -- updated-min: contains an invalid UTC Datetime value - " +
							params.get(UPDATED_MIN) + "&n");
				}

				validParams = false;
			}
		}
		
		// Verify "max-results" query parameter is valid if present
		if(params.get(MAX_RESULTS) != null) {

			// Verify max-results only contains numbers
			if(params.get(MAX_RESULTS).matches(numeric)) validParams = true;

            else {
				if(logger.isInfoEnabled()) {
					logger.info("VerifyEntries -- max-results: contains an invalid numeric value - " +
							params.get(MAX_RESULTS) + "&n");
				}

				validParams = false;
			}
		}
		
		// Verify "start-index" query parameter is valid if present
		if(params.get(START_INDEX) != null) {

			// Verify start-index only contains numbers
			if(params.get(START_INDEX).matches(numeric)) validParams = true;

            else {
				if(logger.isInfoEnabled()) {
					logger.info("VerifyEntries -- start-index: contains an invalid numeric value - " +
							params.get(START_INDEX) + "&n");
				}

				validParams = false;
			}
		}
		
		// Verify "depth" query parameter is valid if present
		if(params.get(DEPTH) != null) {

			// Verify depth only contains numbers
			if(params.get(DEPTH).matches(numeric)) validParams = true;

            else {
				if(logger.isInfoEnabled()) {
					logger.info("VerifyEntries -- depth: contains an invalid numeric value - " +
							params.get(DEPTH) + "&n");
				}

				validParams = false;
			}
		}
		
		return validParams;
		
	}

}
