/*
 *
 *    Copyright (c) 2018-2021 Green Button Alliance, Inc.
 *
 *    Portions (c) 2013-2018 EnergyOS.org
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
 *
 */

package org.greenbuttonalliance.espi.datacustodian.web.api;

import com.sun.syndication.io.FeedException;
import org.greenbuttonalliance.espi.common.domain.usage.ApplicationInformationEntity;
import org.greenbuttonalliance.espi.common.domain.usage.AuthorizationEntity;
import org.greenbuttonalliance.espi.common.domain.usage.RetailCustomerEntity;
import org.greenbuttonalliance.espi.common.service.*;
import org.greenbuttonalliance.espi.common.utils.ExportFilter;
import org.greenbuttonalliance.espi.datacustodian.utils.VerifyURLParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/espi/1_1/resource")
@Tag(name = "Batch Operations", description = "Green Button Bulk Data Processing API")
public class BatchRESTController {

	private final ImportService importService;
	private final ResourceService resourceService;
	private final AuthorizationService authorizationService;
	private final NotificationService notificationService;
	private final RetailCustomerService retailCustomerService;
	private final ExportService exportService;

	@Autowired
	public BatchRESTController(
			ImportService importService,
			ResourceService resourceService,
			AuthorizationService authorizationService,
			NotificationService notificationService,
			RetailCustomerService retailCustomerService,
			ExportService exportService) {
		this.importService = importService;
		this.resourceService = resourceService;
		this.authorizationService = authorizationService;
		this.notificationService = notificationService;
		this.retailCustomerService = retailCustomerService;
		this.exportService = exportService;
	}

	/**
	 * Supports the upload (or import) of Green Button DMD files. Simply send
	 * the DMD file within the POST data.
	 * 
	 * Requires Authorization: Bearer [{data_custodian_access_token} |
	 * {upload_access_token}]
	 * 
	 * @param response
	 *            HTTP Servlet Response
	 * @param retailCustomerId
	 *            The locally unique identifier of a Retail Customer - NOTE PII
	 * @param params
	 *            HTTP Query Parameters
	 * @param stream
	 *            An input stream
     * @throws IOException
     *            Exception thrown by failed or interrupted I/O operations.
     * @throws FeedException
     *            Exception thrown by WireFeedInput, WireFeedOutput, WireFeedParser
     *            and WireFeedGenerator instances if they can not parse or generate a feed.
	 *
     * <p>
     *   Usage:
	 *   POST /espi/1_1/resource/Batch/RetailCustomer/{retailCustomerId}/UsagePoint
     * </p>
	 */
	@PostMapping(value = "/Batch/RetailCustomer/{retailCustomerId}/UsagePoint",
			consumes = MediaType.APPLICATION_ATOM_XML_VALUE,
			produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Bulk Upload Green Button Data",
			description = "Uploads Green Button DMD (Download My Data) files for batch processing. " +
						"Supports bulk import of usage points, meter readings, and interval data."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Bulk upload successful"),
			@ApiResponse(responseCode = "400", description = "Invalid ATOM XML or batch data"),
			@ApiResponse(responseCode = "401", description = "Unauthorized batch operation"),
			@ApiResponse(responseCode = "413", description = "File size exceeds limits")
	})
	public void upload(HttpServletResponse response,
			@Parameter(description = "Retail customer identifier", required = true)
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {

		try {
			RetailCustomerEntity retailCustomer = retailCustomerService
					.findById(retailCustomerId);

			importService.importData(stream, retailCustomerId);

			// do any notifications

			notificationService.notify(retailCustomer,
					importService.getMinUpdated(),
					importService.getMaxUpdated());

		} catch (Exception e) {
			System.out.printf("**** Batch Import Error: %s\n", e.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	/**
	 * Supports Green Button Download My Data - A DMD file will be produced that
	 * contains all Usage Points for the requested Retail Customer.
	 * 
	 * Requires Authorization: Bearer [{data_custodian_access_token} |
	 * {upload_access_token}]
	 * 
	 * @param response
	 *            HTTP Servlet Response
	 * @param retailCustomerId
	 *            The locally unique identifier of a Retail Customer - NOTE PII
	 * @param params
	 *            HTTP Query Parameters
	 * @throws IOException
     *            Exception thrown by failed or interrupted I/O operations.
	 * @throws FeedException
     *            Exception thrown by WireFeedInput, WireFeedOutput, WireFeedParser
     *            and WireFeedGenerator instances if they can not parse or generate a feed.
	 * <p>
     *   Usage:
     *   GET /espi/1_1/resource/Batch/RetailCustomer/{retailCustomerId}/UsagePoint
     * </p>
	 */
	@GetMapping(value = "/Batch/RetailCustomer/{retailCustomerId}/UsagePoint",
			produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Download Green Button Data Collection",
			description = "Downloads all usage points for a retail customer as Green Button DMD file"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "DMD file generated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access")
	})
	public void download_collection(HttpServletResponse response,
			@Parameter(description = "Retail customer identifier", required = true)
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries("/espi/1_1/resource/Batch/RetailCustomer/{retailCustomerId}/UsagePoint", params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		response.addHeader("Content-Disposition",
				"attachment; filename=GreenButtonDownload.xml");
		try {
			// TODO -- need authorization hook
			exportService.exportUsagePointsFull(0L, retailCustomerId,
					response.getOutputStream(), new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	/**
	 * Supports Green Button Download My Data A DMD file for a particular Usage
	 * Point will be produced and returned to the Retail Customer
	 * 
	 * Requires Authorization: Bearer [{data_custodian_access_token} |
	 * {upload_access_token}]
	 * 
	 * @param response
	 *            HTTP Servlet Response
	 * @param retailCustomerId
	 *            The locally unique identifier of a Retail Customer - NOTE PII
	 * @param usagePointId
	 *            The locally unique identifier of a UsagePoint.id
	 * @param params
	 *            HTTP Query Parameters
     * @throws IOException
     *            Exception thrown by failed or interrupted I/O operations.
     * @throws FeedException
     *            Exception thrown by WireFeedInput, WireFeedOutput, WireFeedParser
     *            and WireFeedGenerator instances if they can not parse or generate a feed.
	 *
     * <p>
     *   Usage:
     *   GET /espi/1_1/resource/Batch/RetailCustomer/{retailCustomerId}/UsagePoint/{usagePointId}
     * </p>
	 */
	@GetMapping(value = "/Batch/RetailCustomer/{retailCustomerId}/UsagePoint/{usagePointId}",
			produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Download Green Button Data Member",
			description = "Downloads specific usage point data for a retail customer as Green Button DMD file"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "DMD file generated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "404", description = "Usage point not found")
	})
	public void download_member(HttpServletResponse response,
			@Parameter(description = "Retail customer identifier", required = true)
			@PathVariable Long retailCustomerId,
			@Parameter(description = "Usage point identifier", required = true)
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries("/espi/1_1/resource/Batch/RetailCustomer/{retailCustomerId}/UsagePoint/{usagePointId}", params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		response.addHeader("Content-Disposition",
				"attachment; filename=GreenButtonDownload.xml");
		try {

			exportService.exportUsagePointFull(0L, retailCustomerId,
					usagePointId, response.getOutputStream(), new ExportFilter(
							params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	/**
     * Produce a Subscription for the requester. The resultant response will
     * contain an Atom "feed" of the Usage Point(s) associated with the subscription.
	 *
	 * Requires Authorization: Bearer [{data_custodian_access_token} |
	 * {access_token}]
	 * 
	 * @param response
	 *            HTTP Servlet Response
	 * @param subscriptionId
	 *            Long identifying the Subscription.id of the desired
	 *            Authorization
	 * @param params
	 *            HTTP Query Parameters
     * @throws IOException
     *            Exception thrown by failed or interrupted I/O operations.
     * @throws FeedException
     *            Exception thrown by WireFeedInput, WireFeedOutput, WireFeedParser
     *            and WireFeedGenerator instances if they can not parse or generate a feed.
	 * 
	 * <p>
     *   Usage:
     *   GET /espi/1_1/resource/Batch/Subscription/{subscriptionId}
	 * </p>
	 */
	@Transactional(readOnly = true)
	@GetMapping(value = "/Batch/Subscription/{subscriptionId}",
			produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Download Subscription Data",
			description = "Downloads usage points associated with a subscription as Green Button feed"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Subscription data retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "404", description = "Subscription not found")
	})
	public void subscription(HttpServletResponse response,
			@Parameter(description = "Subscription identifier", required = true)
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries("/espi/1_1/resource/Batch/Subscription/{subscriptionId}", params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		response.addHeader("Content-Disposition",
				"attachment; filename=GreenButtonDownload.xml");
		try {
			exportService.exportBatchSubscription(subscriptionId,
					response.getOutputStream(), new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	/**
     * Produce a Subscription for the requester. The resultant response will
     * contain an Atom "feed" of the Usage Point(s) associated with the subscription.
	 *
	 * Requires Authorization: Bearer [{data_custodian_access_token} |
	 * {access_token}]
	 * 
	 * @param response
	 *            HTTP Servlet Response
	 * @param subscriptionId
	 *            Long identifying the Subscription.id of the desired
	 *            Authorization
	 * @param params
	 *            HTTP Query Parameters
     * @throws IOException
     *            Exception thrown by failed or interrupted I/O operations.
     * @throws FeedException
     *            Exception thrown by WireFeedInput, WireFeedOutput, WireFeedParser
     *            and WireFeedGenerator instances if they can not parse or generate a feed.
	 *
     * <p>
     *   Usage:
	 *   GET /espi/1_1/resource/Batch/Subscription/{subscriptionId}/UsagePoint
     * </p>
	 */
	@Transactional(readOnly = true)
	@GetMapping(value = "/Batch/Subscription/{subscriptionId}/UsagePoint",
			produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Download Subscription Usage Points",
			description = "Downloads all usage points for a specific subscription as Green Button feed"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usage point data retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "404", description = "Subscription not found")
	})
	public void subscriptionUsagePoint(HttpServletResponse response,
			@Parameter(description = "Subscription identifier", required = true)
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries("/espi/1_1/resource/Batch/Subscription/{subscriptionId}/UsagePoint", params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		response.addHeader("Content-Disposition",
				"attachment; filename=GreenButtonDownload.xml");
		try {
			exportService.exportBatchSubscriptionUsagePoint(subscriptionId,
					response.getOutputStream(), new ExportFilter(params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	/**
     * Produce a Subscription for the requester. The resultant response will
     * contain an Atom "feed" of the Usage Point(s) associated with the subscription.
     *
	 * Requires Authorization: Bearer [{data_custodian_access_token} |
	 * {access_token}]
	 * 
	 * @param response
	 *            HTTP Servlet Response
	 * @param subscriptionId
	 *            Long identifying the Subscription.id of the desired
	 *            Authorization
	 * @param usagePointId
	 *            Long identifying the UsagePoint.id of the desired
	 *            Authorization
	 * @param params
	 *            HTTP Query Parameters
     * @throws IOException
     *            Exception thrown by failed or interrupted I/O operations.
     * @throws FeedException
     *            Exception thrown by WireFeedInput, WireFeedOutput, WireFeedParser
     *            and WireFeedGenerator instances if they can not parse or generate a feed.
	 *
     * <p>
     *   Usage:
	 *   GET /espi/1_1/resource/Batch/Subscription/{subscriptionId}
     * </p>
	 */

	@Transactional(readOnly = true)
	@GetMapping(value = "/Batch/Subscription/{subscriptionId}/UsagePoint/{usagePointId}",
			produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Download Specific Subscription Usage Point",
			description = "Downloads a specific usage point for a subscription as Green Button feed"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usage point data retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access"),
			@ApiResponse(responseCode = "404", description = "Subscription or usage point not found")
	})
	public void subscriptionUsagePointMember(HttpServletResponse response,
			@Parameter(description = "Subscription identifier", required = true)
			@PathVariable Long subscriptionId,
			@Parameter(description = "Usage point identifier", required = true)
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries("/espi/1_1/resource/Batch/Subscription/{subscriptionId}/UsagePoint/{usagePointId}", params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
		response.addHeader("Content-Disposition",
				"attachment; filename=GreenButtonDownload.xml");
		try {
			exportService.exportBatchSubscriptionUsagePoint(subscriptionId,
					usagePointId, response.getOutputStream(), new ExportFilter(
							params));

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	/**
	 * Provide a Bulk delivery of information. The Third Party is provided with
	 * the XML representation of the Bulk.
	 * 
	 * RESTful Pattern: /espi/1_1/resource/Batch/Subscription/{subscriptionId}
	 * 
	 * Requires Authorization: Bearer [{data_custodian_access_token} |
	 * {client_access_token}]
	 * 
	 * @param request
	 *            HTTP Servlet Request
	 * @param response
	 *            HTTP Servlet Response
	 * @param bulkId
	 *            Long identifying the BR={bulkId} within the
	 *            Authorization.scope string
	 * @param params
	 *            HTTP Query Parameters
     * @throws IOException
     *            Exception thrown by failed or interrupted I/O operations.
     * @throws FeedException
     *            Exception thrown by WireFeedInput, WireFeedOutput, WireFeedParser
     *            and WireFeedGenerator instances if they can not parse or generate a feed.
	 *
     * <p>
     *   Usage:
	 *   GET /espi/1_1/resource/Batch/Bulk/{bulkId}
     * </p>
	 */
	@GetMapping(value = "/Batch/Bulk/{bulkId}",
			produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@Operation(
			summary = "Bulk Data Delivery",
			description = "Provides bulk delivery of information for third-party applications. " +
						"Supports both HTTPS and SFTP delivery methods."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Bulk data delivered successfully"),
			@ApiResponse(responseCode = "202", description = "Bulk data queued for SFTP delivery"),
			@ApiResponse(responseCode = "400", description = "Invalid request parameters"),
			@ApiResponse(responseCode = "401", description = "Unauthorized access")
	})
	// TODO Add support for wildcard bulkId parameters
	public void bulk(HttpServletRequest request, HttpServletResponse response,
			@Parameter(description = "Bulk identifier", required = true)
			@PathVariable Long bulkId,
			@RequestParam Map<String, String> params)
			throws IOException, FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries("/espi/1_1/resource/Batch/Bulk/{bulkId}", params)) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request contains invalid query parameter values!");
			return;
		}

		// check to see if SFTP (or XML Caching) is turned on for this bulkId.
		//
		if (isSFTP(request)) {
			if (isInCache(request, bulkId)) {
				// make sure the sftpd is running
				// send back a 302 and queue up a notification
				// to initiate the SFTP (may want to log this)
			} else {
				// build the file stream
				// in parallel
				// generate the xml to a file
				// return 302 response code
				// TODO: make SFTP cache location an configuration
				File destinationFile = new File(
						"/var/greenbutton/export-cache/" + bulkId);
				FileOutputStream fileOutputStream = new FileOutputStream(
						destinationFile);

				try {
					try {
						exportService.exportBatchBulk(bulkId,
								getAuthorizationThirdParty(request),
								fileOutputStream, new ExportFilter(params));

					} catch (Exception e) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					if (destinationFile != null) {
						try {
							fileOutputStream.close();
							response.setStatus(HttpServletResponse.SC_ACCEPTED);
						} catch (IOException e) {
							// Ignore issues during closing
						}
					}
				}

			}
		} else {
			// not SFTP, use HTTPS as bulk response

			response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
			// note this default to a file just in case the handler doesn't want
			// to directly
			// parse the incoming stream.

			response.addHeader("Content-Disposition",
					"attachment; filename=GreenButtonDownload.xml");

			try {
				exportService.exportBatchBulk(bulkId,
						getAuthorizationThirdParty(request),
						response.getOutputStream(), new ExportFilter(params));

			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}

		}

	}

	private String getAuthorizationThirdParty(HttpServletRequest request) {
		String token = request.getHeader("authorization");
		String thirdParty = "";

		if (token != null) {
			token = token.replace("Bearer ", "");
			AuthorizationEntity authorization = authorizationService
					.findByAccessToken(token);
			ApplicationInformationEntity applicationInformation = authorization
					.getApplicationInformation();
			// note that ApplicationInformation.clientId is a String
			//
			thirdParty = applicationInformation.getClientId();
		}

		return thirdParty;

	}

	private boolean isInCache(HttpServletRequest request, Long bulkId) {
		System.out
				.println(System.getProperty("/var/greenbutton/export-cache/"));
		System.out.println(System.getProperty("/var/greenbutton/export-cache/"
				+ bulkId));
		return false;

	}

	private boolean isSFTP(HttpServletRequest request) {
		Boolean result = false;
		String accessToken = request.getHeader("authorization");
		if (accessToken != null) {
			if (accessToken.contains("Bearer")) {
				// has Authorization header with Bearer type
				accessToken = accessToken.replace("Bearer ", "");
				// ensure length is >12 characters (48 bits in hex at least)
				if (accessToken.length() >= 12) {
					// we have a valid token
					AuthorizationEntity authorization = authorizationService
							.findByAccessToken(accessToken);
					ApplicationInformationEntity applicationInformation = authorization
							.getApplicationInformation();
					String bulkRequestUri = applicationInformation
							.getDataCustodianBulkRequestURI();
					if (bulkRequestUri.contains("sftp:")) {
						result = true;
					}
				}
			}
		}
		return result;
	}


}
