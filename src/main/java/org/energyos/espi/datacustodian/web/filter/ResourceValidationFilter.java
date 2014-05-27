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
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.common.service.UsagePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

public class ResourceValidationFilter implements Filter{

	@Autowired
	SubscriptionService subscriptionService;
	
	@Autowired
	AuthorizationService authorizationService;
	
	@Autowired
	UsagePointService usagePointService;
	
    @Override
    public void destroy() {
        // Do nothing
    }

    @Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		System.out.println("ResourceValidationFilter: start of doFilter");

		Boolean invalid = true;
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		Boolean hasBearer = false;
		Boolean hasValidOAuthAccessToken = false;
		Authorization authorizationFromToken = null;
		Subscription subscription = null;
		String resourceUri=null;
		String authorizationUri=null;

		// get the uri for later tests
		String uri = request.getRequestURI();
		String service = request.getMethod();

		// see if any authentication has happened
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

		System.out.printf("ResourceValidationFilter: role=%s, uri:%s\n", roles, uri);
		
		///////////////////////////////////////////////////////////////////////
        // find the access token if present and validate we have a good one
		///////////////////////////////////////////////////////////////////////
		String token = request.getHeader("authorization");
		if(token!=null)
		{
			if (token.contains("Bearer"))
			{
				// has Authorization header with Bearer type
				hasBearer = true;
				token = token.replace("Bearer ", "");
				// ensure length is >12 characters (48 bits in hex at least)
				if(token.length()>=12)
				{
					// lookup the authorization -- we must have one to correspond to an access token
					try {
						authorizationFromToken = authorizationService.findByAccessToken(token);
					}
					catch (Exception e) {
						System.out
								.printf("ResourceValidationFilter: doFilter - No Authorization Found - %s\n",
										e.toString());
						
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No Authorization Found");
						return;
						
						
					}
					
					// see if we have valid authorization and can get parameters
					if(authorizationFromToken != null)
					{
						hasValidOAuthAccessToken = true;
						resourceUri = authorizationFromToken.getResourceURI();
						authorizationUri = authorizationFromToken.getAuthorizationURI();
						subscription = authorizationFromToken.getSubscription();
					}
				}
			}
		}
		
		///////////////////////////////////////////////////////////////////////
		// if this is not RESTful request with Bearer token
		///////////////////////////////////////////////////////////////////////
		if (hasBearer==false)
		{
			// no bearer token and it passed the OAuth filter - so it must be good2go not RESTAPI request
			invalid = false;
		}
		///////////////////////////////////////////////////////////////////////
		// if this is rest, process based on ROLE
		///////////////////////////////////////////////////////////////////////
		else if(hasValidOAuthAccessToken == true)
		{
			// if it has a valid access token 
			// then we know it is REST request
			// Dispatch on authentication type
		    
		    // strip off uri up to /resource/ since that is how we go here
			resourceUri 		= resourceUri.substring(resourceUri.indexOf("/resource/"));
			authorizationUri 	= authorizationUri.substring(authorizationUri.indexOf("/resource/"));
			uri 				= uri.substring(uri.indexOf("/resource/"));

			///////////////////////////////////////////////////////////////////////
			// ROLE_USER
			// GET /resource/LocalTimeParameters
			// GET /resource/LocalTimeParameters/{LocalTimeParametersID}
			// GET /resource/ReadingType
			// GET /resource/ReadingType/{ReadingTypeID}
			// GET /resource/Batch/Subscription/{SubscriptionID}
			// GET /resource/Batch/Subscription/{SubscriptionID}/UsagePoint/{UsagePointId}
			// GET /resource/Subscription/{SubscriptionID}/UsagePoint
			// GET /resource/Subscription/{SubscriptionID}/UsagePoint/{UsagePointID}
			// GET /resource/Subscription/{SubscriptionID}/UsagePoint/{UsagePointID}/ElectricPowerQualitySummary
			// GET /resource/Subscription/{SubscriptionID}/UsagePoint/{UsagePointID}/ElectricPowerQualitySummary/{ElectricPowerQualitySummaryID}
			// GET /resource/Subscription/{SubscriptionID}/UsagePoint/{UsagePointID}/ElectricPowerUsageSumary
			// GET /resource/Subscription/{SubscriptionID}/UsagePoint/{UsagePointID}/ElectricPowerUsageSumary/{ElectricPowerUsageSummaryID}
			// GET /resource/Subscription/{SubscriptionID}/UsagePoint/{UsagePointID}/MeterReading
			// GET /resource/Subscription/{SubscriptionID}/UsagePoint/{UsagePointID}/MeterReading/{MeterReadingID}
			// GET /resource/Subscription/{SubscriptionID}/UsagePoint/{UsagePointID}/MeterReading/{MeterReadingID}/IntervalBlock
			// GET /resource/Subscription/{SubscriptionID}/UsagePoint/{UsagePointID}/MeterReading/{MeterReadingID}/IntervalBlock/{IntervalBlockID}
			//
			///////////////////////////////////////////////////////////////////////
			if (invalid && roles.contains("ROLE_USER")) {

				// break uri into tokens
				String[] tokens = uri.split("/");
				
				// only GET for this ROLE permitted
				if (!service.equals("GET")) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not authorized");
					return;
				}

				// look for the root forms of LocalTimeParameters and ReadingType
				if (invalid && ((uri.contains("resource/LocalTimeParameters"))
						     || (uri.contains("resource/ReadingType")))) {
					invalid = false;
				}
				
				// must be either /resource/Batch/Subscription/{subscriptionId}
				if (invalid && uri.contains("/resource/Batch/Subscription")) {
					if (uri.equals(resourceUri)) {
						invalid = false;
					} else {
						// not authorized for this resource
						System.out.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Access Not Authorized");
					}
				}				

				// or /resource/Batch/Subscription/{subscriptionId}/**
				if (invalid && uri.contains("/resource/Subscription")) {
					if( authorizationFromToken.getSubscription().getId().compareTo(Long.parseLong(tokens[3],10)) == 0) {
								invalid = false;
					} else {
						// not authorized for this resource
						System.out.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Access Not Authorized");
					}
				}
			
			} else if (invalid && roles.contains("ROLE_DC_ADMIN")) {
				///////////////////////////////////////////////////////////////////////
				// ROLE_DC_ADMIN
				// Access to all services and APIs
				///////////////////////////////////////////////////////////////////////
				//
				invalid = false;

			} else if (invalid && roles.contains("ROLE_TP_ADMIN")) {
				///////////////////////////////////////////////////////////////////////
				// ROLE_TP_ADMIN
				// GET /resource/Authorization
				// GET /resource/Authorization/{AuthorizationID}
				// GET /resource/Batch/Bulk/{BulkID}
				// GET /resource/ReadServiceStatus
				// GETALL sftp://services.greenbuttondata.org/DataCustodian/espi/1_1/resource/Batch/Bulk/{BulkID}
				///////////////////////////////////////////////////////////////////////

				// only GET for this ROLE permitted
				if (!(service.equals("GET") || service.equals("GETALL"))) {
					System.out.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not authorized");
					return;
				}
				
				
				// the Bulk Authorizations
				if (invalid && uri.contains("/resource/Batch/Bulk")) {
					//TODO
				}
				
				// Authorizations
				if (invalid && (uri.contains("/resource/Authorization"))){
					// this is retrieving the authorization related to the token
					// the TP has access to AuthorizationIds he is tp for
					// which is authorization looked by access token, or,
					// any authorization that points to same applicationInformationId
					
					if(authorizationUri.equals(uri)) {
						invalid=false;
					} else {
						// get authorizationID
						String[] tokens = uri.split("/");
						Long authorizationId = Long.parseLong(tokens[3],10);

						if (authorizationId != null) {
							Authorization requestedAuthorization = authorizationService
									.findById(authorizationId);
							if (requestedAuthorization.getApplicationInformation().getId() == authorizationFromToken.getApplicationInformation().getId()) {
								invalid = false;
							} else {
								// not authorized for this resource
								System.out.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
								response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Access Not Authorized");
							}
						} else {
							// this is collection request and controller will limit to valid authorizations
							// for this ThirdParty
							// TODO check
							invalid = false;
						}
					}
				}
				
				if (invalid && uri.contains("/resource/ReadServiceStatus")) {
					// the Bulk Authorizations
					invalid = false;
				}
				
				
				// sftp
				// TODO:
				
			} else if (invalid && roles.contains("ROLE_UL_ADMIN")) {
				///////////////////////////////////////////////////////////////////////
				// ROLE_UL_ADMIN
				//
				// GET  /resource/Batch/RetailCustomer/{RetailCustomerID}/UsagePoint
				// POST /resource/Batch/RetailCustomer/{RetailCustomerID}/UsagePoint
				// GET /resource/Authorization/{AuthorizationID}
				//
				///////////////////////////////////////////////////////////////////////

				// he can get his own AuthorizationUri
				// check if it is this authorization request
				if (uri.contains("/resource/Authorization")) {
					if(authorizationUri.equals(uri)) {
						invalid=false;
					}
					else {
						// not authorized for this resource
						System.out.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Access Not Authorized");
					}
				}
				
				if (invalid && uri.contains("/resource/Batch/RetailCustomer")) {
						invalid = false;
				}
		    }
			else if (invalid && roles.contains("ROLE_REGISTRATION"))	{
				///////////////////////////////////////////////////////////////////////
				// ROLE_REGISTRATION_ADMIN
				// GET /resource/ApplicationInformation/{ApplicationInformationID}
				// GET /resource/Authorization/{AuthorizationID}
				//
				///////////////////////////////////////////////////////////////////////

				// only GET for this ROLE permitted
				if (!service.equals("GET")) {
					System.out.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not authorized");
					return;
				}
				
				if (uri.contains("/resource/ApplicationInformation")) {
					String[] tokens = uri.split("/");
					Long applicationInformationIdFromUri = Long.parseLong(tokens[3],10);
					Long applicationInformationId = authorizationFromToken.getApplicationInformation().getId();
					
					// only gets access to his
					if(applicationInformationIdFromUri == applicationInformationId){
						invalid = false;
					}
					else{
						// not authorized for this resource
						System.out.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Access Not Authorized");
					}
				}

				// check if it is this authorization request
				if (uri.contains("/resource/Authorization")) {
					if(authorizationUri.equals(uri)) {
						invalid=false;
					}
					else {
						// not authorized for this resource
						System.out.printf("ResourceValidationFilter: doFilter - Access Not Authorized\n");
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Access Not Authorized");
					}
				}
			}
		}

		
		System.out.printf("ResourceValidationFilter: normal exit doFilter: invalid=%s\n", invalid);
		
		if (invalid) return;
		
		chain.doFilter(req, res);

	}

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // nothing to do here
    }

}
