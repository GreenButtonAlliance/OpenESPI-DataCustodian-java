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

package org.energyos.espi.datacustodian.web.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.energyos.espi.common.domain.ApplicationInformation;
import org.energyos.espi.common.domain.Authorization;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.ImportService;
import org.energyos.espi.common.service.NotificationService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.utils.ExportFilter;
import org.energyos.espi.datacustodian.utils.VerifyURLParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import com.sun.syndication.io.FeedException;

@Controller
public class BatchRESTController {

	@Autowired
	private ImportService importService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private RetailCustomerService retailCustomerService;

	@Autowired
	private ExportService exportService;

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
	 * @throws FeedException
	 * 
	 * @usage POST
	 *        /espi/1_1/resource/Batch/RetailCustomer/{retailCustomerId}/UsagePoint
	 */
	@RequestMapping(value = Routes.BATCH_UPLOAD_MY_DATA, method = RequestMethod.POST, consumes = "application/xml", produces = "application/atom+xml")
	@ResponseBody
	public void upload(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params, InputStream stream)
			throws IOException, FeedException {

		try {
			RetailCustomer retailCustomer = retailCustomerService
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
	 * @param HTTP
	 *            Query Parameters
	 * @throws IOException
	 * @throws FeedException
	 * 
	 * @usage GET
	 *        /espi/1_1/resource/Batch/RetailCustomer/{retailCustomerId}/UsagePoint
	 */
	@RequestMapping(value = Routes.BATCH_DOWNLOAD_MY_DATA_COLLECTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void download_collection(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.BATCH_DOWNLOAD_MY_DATA_COLLECTION, params)) {

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
	 *            params HTTP Query Parameters
	 * @throws IOException
	 * @throws FeedException
	 * 
	 * @usage GET
	 *        /espi/1_1/resource/Batch/RetailCustomer/{retailCustomerId}/UsagePoint
	 *        /{usagePointId}
	 */
	@RequestMapping(value = Routes.BATCH_DOWNLOAD_MY_DATA_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void download_member(HttpServletResponse response,
			@PathVariable Long retailCustomerId,
			@PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.BATCH_DOWNLOAD_MY_DATA_MEMBER, params)) {

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
	 * contain a <feed> of the Usage Point(s) associated with the subscription.
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
	 * @throws FeedException
	 * 
	 * @usage GET /espi/1_1/resource/Batch/Subscription/{subscriptionId}
	 */
	@Transactional(readOnly = true)
	@RequestMapping(value = Routes.BATCH_SUBSCRIPTION, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void subscription(HttpServletResponse response,
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.BATCH_SUBSCRIPTION, params)) {

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
	 * contain a <feed> of the Usage Point(s) associated with the subscription.
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
	 * @throws FeedException
	 * 
	 * @usage GET
	 *        /espi/1_1/resource/Batch/Subscription/{subscriptionId}/UsagePoint
	 */
	@Transactional(readOnly = true)
	@RequestMapping(value = Routes.BATCH_SUBSCRIPTION_USAGEPOINT, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void subscriptionUsagePoint(HttpServletResponse response,
			@PathVariable Long subscriptionId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.BATCH_SUBSCRIPTION_USAGEPOINT, params)) {

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
	 * contain a <feed> of the Usage Point(s) associated with the subscription.
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
	 * @throws FeedException
	 * 
	 * @usage GET /espi/1_1/resource/Batch/Subscription/{subscriptionId}
	 */

	@Transactional(readOnly = true)
	@RequestMapping(value = Routes.BATCH_SUBSCRIPTION_USAGEPOINT_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void subscriptionUsagePointMember(HttpServletResponse response,
			@PathVariable Long subscriptionId, @PathVariable Long usagePointId,
			@RequestParam Map<String, String> params) throws IOException,
			FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.BATCH_SUBSCRIPTION_USAGEPOINT_MEMBER, params)) {

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
	 * @throws FeedException
	 * 
	 * @usage GET /espi/1_1/resource/Batch/Bulk/{bulkId}
	 */
	@RequestMapping(value = Routes.BATCH_BULK_MEMBER, method = RequestMethod.GET, produces = "application/atom+xml")
	@ResponseBody
	public void bulk(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long bulkId, @RequestParam Map<String, String> params)
			throws IOException, FeedException {

		// Verify request contains valid query parameters
		if(!VerifyURLParams.verifyEntries(Routes.BATCH_BULK_MEMBER, params)) {

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
			Authorization authorization = authorizationService
					.findByAccessToken(token);
			ApplicationInformation applicationInformation = authorization
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
					Authorization authorization = authorizationService
							.findByAccessToken(accessToken);
					ApplicationInformation applicationInformation = authorization
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

	public void setImportService(ImportService importService) {
		this.importService = importService;
	}

	public ImportService getImportService() {
		return this.importService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public ResourceService getResourceService() {
		return this.resourceService;
	}

	public void setAuthorizationService(
			AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	public AuthorizationService getAuthorizationService() {
		return this.authorizationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public NotificationService getNotificationService() {
		return this.notificationService;
	}

	public void setRetailCustomerService(
			RetailCustomerService retailCustomerService) {
		this.retailCustomerService = retailCustomerService;
	}

	public RetailCustomerService getRetailCustomerService() {
		return this.retailCustomerService;
	}

	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	public ExportService getExportService() {
		return this.exportService;
	}

}
