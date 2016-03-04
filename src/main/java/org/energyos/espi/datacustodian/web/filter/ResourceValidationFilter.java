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

package org.energyos.espi.datacustodian.web.filter;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.energyos.espi.common.domain.Authorization;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.common.service.UsagePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

public class ResourceValidationFilter implements Filter {

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private UsagePointService usagePointService;

	@Override
	public void destroy() {
		// Do nothing
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		System.out.println("ResourceValidationFilter: start of doFilter");

		Boolean invalid = true;
		Boolean hasBearer = false;
		Boolean hasValidOAuthAccessToken = false;
		Boolean resourceRequest = false;

		Authorization authorizationFromToken = null;
		Subscription subscription = null;
		String resourceUri = null;
		String authorizationUri = null;
		Set<String> roles = null;

		// Get the URI for later tests
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String uri = request.getRequestURI();
		String service = request.getMethod();

		// See if any authentication has happened
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (authentication != null) {
			roles = AuthorityUtils.authorityListToSet(authentication
					.getAuthorities());

		} else {
			System.out
					.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
			throw new AccessDeniedException(
					String.format("Access Not Authorized"));

		}

		System.out
				.printf("ResourceValidationFilter: role = %s, HttpRequest Method: %s, uri: %s\n",
						roles, service, uri);

		// Only process resource API request

		if ((uri.indexOf("/espi/1_1/resource/") != -1)) {
			resourceRequest = true;
		}

		// /////////////////////////////////////////////////////////////////////
		// find the access token if present and validate we have a good one
		// /////////////////////////////////////////////////////////////////////
		String token = request.getHeader("authorization");

		if (token != null) {
			if (token.contains("Bearer")) {
				// has Authorization header with Bearer type
				hasBearer = true;
				token = token.replace("Bearer ", "");

				// ensure length is >12 characters (48 bits in hex at least)
				if (token.length() >= 12) {
					// lookup the authorization -- we must have one to
					// correspond to an access token
					try {
						authorizationFromToken = authorizationService
								.findByAccessToken(token);

						hasValidOAuthAccessToken = true;
						resourceUri = authorizationFromToken.getResourceURI();
						authorizationUri = authorizationFromToken
								.getAuthorizationURI();
						subscription = authorizationFromToken.getSubscription();

					} catch (Exception e) {
						System.out
								.printf("ResourceValidationFilter: doFilter - No Authorization Found - %s\n",
										e.toString());
						throw new AccessDeniedException(
								String.format("No Authorization Found"));
					}
				}
			}
		}

		// /////////////////////////////////////////////////////////////////////
		// If this is a resource request ensure it has a Bearer token
		// /////////////////////////////////////////////////////////////////////
		if ((hasBearer == false) & !(resourceRequest == true)) {
			// no Bearer token and it passed the OAuth filter - so it must be
			// good2go not RESTAPI request
			// make sure the role is not an ANONYMOUS request for /manage ...
			if (!((roles.contains("ROLE_ANONYMOUS")) & (uri
					.indexOf("/management") != -1))) {
				invalid = false;
			}

		}
		// /////////////////////////////////////////////////////////////////////
		// if this is RESTful request, process based on ROLE
		// /////////////////////////////////////////////////////////////////////
		else if (hasValidOAuthAccessToken == true) {
			// if it has a valid access token
			// then we know it is REST request
			// Dispatch on authentication type

			// first, escape out of this if it is the special
			// "manage?command=foo" form
			int i = uri.indexOf("/management");
			if (i > 0) {
				if (roles.contains("ROLE_DC_ADMIN")) {
					invalid = false;
				} else {
					System.out
							.printf("ResourceValidationFilter: doFilter - not valid for this token %s\n",
									uri);
					throw new AccessDeniedException(
							String.format("Access Not Authorized"));
				}
			} else {
				// lets check the uri
				i = uri.indexOf("espi/1_1/resource/");
				if (i > 0) {
					// lets shorten it by stripping off up to resource
					uri = uri.substring(uri.indexOf("/resource/"));
				} else {
					// cant be a resource
					System.out
							.printf("ResourceValidationFilter: doFilter - Uri not well formed %s\n",
									uri);
					throw new AccessDeniedException(
							String.format("Access Not Authorized"));
				}
			}

			// strip off uri up to /resource/ since that is how we go here
			resourceUri = resourceUri.substring(resourceUri
					.indexOf("/resource/"));
			authorizationUri = authorizationUri.substring(authorizationUri
					.indexOf("/resource/"));

			// /////////////////////////////////////////////////////////////////////
			// ROLE_USER
			//
			// GET /resource/LocalTimeParameters
			// GET /resource/LocalTimeParameters/{localTimeParametersId}
			// GET /resource/ReadingType
			// GET /resource/ReadingType/{readingTypeId}
			// GET /resource/Batch/Subscription/{subscriptionId}
			// GET
			// /resource/Batch/Subscription/{subscriptionId}/UsagePoint/{usagePointId}
			// GET /resource/Subscription/{subscriptionId}/UsagePoint
			// GET
			// /resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}
			// GET
			// /resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/ElectricPowerQualitySummary
			// GET
			// /resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/ElectricPowerQualitySummary/{electricPowerQualitySummaryId}
			// GET
			// /resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/ElectricPowerUsageSumary
			// GET
			// /resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/ElectricPowerUsageSumary/{electricPowerUsageSummaryId}
			// GET
			// /resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading
			// GET
			// /resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}
			// GET
			// /resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}/IntervalBlock
			// GET
			// /resource/Subscription/{subscriptionId}/UsagePoint/{usagePointId}/MeterReading/{meterReadingId}/IntervalBlock/{intervalBlockId}
			//
			// /////////////////////////////////////////////////////////////////////
			if (invalid && roles.contains("ROLE_USER")) {

				// break uri into tokens
				String[] tokens = uri.split("/");

				// only GET for this ROLE permitted
				if (!service.equals("GET")) {
					System.out
							.printf("ResourceValidationFilter: doFilter - ROLE_USER attempted a RESTful %s Request -- Only GET Request are allowed\n",
									service);
					throw new AccessDeniedException(
							String.format("Access Not Authorized"));
				}

				// look for the root forms of LocalTimeParameters and
				// ReadingType
				if (invalid
						&& ((uri.contains("resource/LocalTimeParameters")) || (uri
								.contains("resource/ReadingType")))) {
					invalid = false;
				}

				// must be either /resource/Batch/Subscription/{subscriptionId}
				if (invalid && uri.contains("/resource/Batch/Subscription")) {
					if (uri.startsWith(resourceUri)) {
						invalid = false;
					} else {
						// not authorized for this resource
						System.out
								.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
						throw new AccessDeniedException(
								String.format("Access Not Authorized"));
					}
				}

				// or /resource/Batch/Subscription/{subscriptionId}/**
				if (invalid && uri.contains("/resource/Subscription")) {
					if (authorizationFromToken.getSubscription().getId()
							.compareTo(Long.parseLong(tokens[3], 10)) == 0) {
						invalid = false;
					} else {
						// not authorized for this resource
						System.out
								.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
						throw new AccessDeniedException(
								String.format("Access Not Authorized"));
					}
				}

			} else if (invalid && roles.contains("ROLE_DC_ADMIN")) {
				// /////////////////////////////////////////////////////////////////////
				// ROLE_DC_ADMIN
				// Access to all services and APIs
				// /////////////////////////////////////////////////////////////////////
				//
				invalid = false;

			} else if (invalid && roles.contains("ROLE_TP_ADMIN")) {
				// /////////////////////////////////////////////////////////////////////
				// ROLE_TP_ADMIN
				//
				// GET /resource/Authorization
				// GET /resource/Authorization/{authorizationId}
				// PUT /resource/Authorization/{authorizationId}
				// DELETE /resource/Authorization/{authorizationId}
				// GET /resource/Batch/Bulk/{bulkId}
				// GET /resource/ReadServiceStatus
				// GETALL
				// sftp://services.greenbuttondata.org/DataCustodian/espi/1_1/resource/Batch/Bulk/{bulkId}
				// /////////////////////////////////////////////////////////////////////

				// the Bulk Authorizations
				if (invalid && uri.contains("/resource/Batch/Bulk")) {
					invalid = false;
				}

				// Authorizations GET/PUT/DELETE on individual, GET on
				// collection
				if (invalid && (uri.contains("/resource/Authorization"))) {
					// this is retrieving the authorization related to the token
					// the TP has access to AuthorizationIds he is tp for
					// which is authorization looked by access token, or,
					// any authorization that points to same
					// applicationInformationId

					if (service.equals("GET") || service.equals("PUT")
							|| service.equals("DELETE")) {
						if (authorizationUri.equals(uri)) {
							invalid = false;
						} else {
							// get authorizationID if present
							String[] tokens = uri.split("/");
							Long authorizationId = -1l;
							if (tokens.length > 3) {
								authorizationId = Long.parseLong(tokens[3], 10);
							}

							// check if its a collection
							if (authorizationId != -1l) {
								// it is specific ID, see if it authorization
								// for this third party
								Authorization requestedAuthorization = authorizationService
										.findById(authorizationId);
								if ((requestedAuthorization
										.getApplicationInformation().getId())
										.equals(authorizationFromToken
												.getApplicationInformation()
												.getId())) {
									invalid = false;
								} else {
									// not authorized for this resource
									System.out
											.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
									throw new AccessDeniedException(
											String.format("Access Not Authorized"));
								}
							} else {
								// this is collection request and controller
								// will limit to valid authorizations
								if (service.equals("GET")) {
									invalid = false;
								} else {
									// not authorized for this resource
									System.out
											.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
									throw new AccessDeniedException(
											String.format("Access Not Authorized"));
								}
							}
						}
					}
				}

				if (invalid && uri.contains("/resource/ReadServiceStatus")) {
					if (service.equals("GET")) {
						invalid = false;
					} else {
						// not authorized for this resource
						System.out
								.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
						throw new AccessDeniedException(
								String.format("Access Not Authorized"));
					}
				}

				// sftp
				// TODO:

			} else if (invalid && roles.contains("ROLE_UL_ADMIN")) {
				// /////////////////////////////////////////////////////////////////////
				// ROLE_UL_ADMIN
				//
				// GET
				// /resource/Batch/RetailCustomer/{retailCustomerId}/UsagePoint
				// POST
				// /resource/Batch/RetailCustomer/{retailCustomerId}/UsagePoint
				// GET /resource/Authorization/{authorizationId}
				//
				// /////////////////////////////////////////////////////////////////////

				// he can get his own AuthorizationUri
				// check if it is this authorization request
				if (uri.contains("/resource/Authorization")) {
					if (authorizationUri.equals(uri)) {
						invalid = false;
					} else {
						// not authorized for this resource
						System.out
								.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
						throw new AccessDeniedException(
								String.format("Access Not Authorized"));
					}
				}

				if (invalid && uri.contains("/resource/Batch/RetailCustomer")) {
					invalid = false;
				}

				if (invalid && uri.contains("/resource/ReadServiceStatus")) {
					if (service.equals("GET")) {
						invalid = false;
					} else {
						// not authorized for this resource
						System.out
								.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
						throw new AccessDeniedException(
								String.format("Access Not Authorized"));
					}
				}
			} else if (invalid && roles.contains("ROLE_TP_REGISTRATION")) {
				// /////////////////////////////////////////////////////////////////////
				// ROLE_TP_REGISTRATION
				//
				// GET
				// /resource/ApplicationInformation/{applicationInformationId}
				// PUT
				// /resource/ApplicationInformation/{applicationInformationId}
				// DELETE
				// /resource/ApplicationInformation/{applicationInformationId}
				// /////////////////////////////////////////////////////////////////////

				if (service.equals("GET") || service.equals("PUT")
						|| service.equals("DELETE")) {
					if (uri.contains("/resource/ApplicationInformation")) {
						String[] tokens = uri.split("/");

						if (tokens.length > 3) {
							Long applicationInformationIdFromUri = Long
									.parseLong(tokens[3], 10);
							Long applicationInformationId = authorizationFromToken
									.getApplicationInformation().getId();

							// only gets access to his
							if (applicationInformationIdFromUri
									.equals(applicationInformationId)) {
								invalid = false;
							}

						} else {
							// not authorized for this resource
							System.out
									.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
							throw new AccessDeniedException(
									String.format("Access Not Authorized"));
						}

					} else {
						// not authorized for this resource
						System.out
								.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
						throw new AccessDeniedException(
								String.format("Access Not Authorized"));
					}

				} else {
					// not authorized for this resource
					System.out
							.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
					throw new AccessDeniedException(
							String.format("Access Not Authorized"));
				}
			}
		}

		System.out.printf(
				"ResourceValidationFilter: normal exit doFilter: invalid=%s\n",
				invalid);

		if (invalid) {
			// not authorized for this resource
			System.out
					.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
			throw new AccessDeniedException(
					String.format("Access Not Authorized"));
		}

		// TODO -- Verify contents of query parameters are properly formatted
		
		chain.doFilter(req, res);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// nothing to do here
	}

	public void setSubscriptionService(SubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	public SubscriptionService getSubscriptionService() {
		return this.subscriptionService;
	}

	public void setAuthorizationService(
			AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	public AuthorizationService getAuthorizationService() {
		return this.authorizationService;
	}

	public void setUsagePointService(UsagePointService usagePointService) {
		this.usagePointService = usagePointService;
	}

	public UsagePointService getUsagePointService() {
		return this.usagePointService;
	}

}
