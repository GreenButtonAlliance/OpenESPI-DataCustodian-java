package org.energyos.espi.datacustodian.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

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

		System.out.println("ResourceValidationFilter: doFilter");

		Boolean invalid = true;
		HttpServletRequest request = (HttpServletRequest) req;

		Boolean hasBearer = false;
		Boolean hasValidOAuthAccessToken = false;
		Authorization authorization = null;
		Subscription subscription = null;
		String resourceUri=null;

		// get the uri for later tests
		String uri = request.getRequestURI();

		// see if any authentication has happened
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

		System.out.printf("ResourceValidationFilter: role=%s\n", roles);
		
        // find the access token if present and validate we have a good one
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
						authorization = authorizationService.findByAccessToken(token);
					}
					catch (Exception e) {
						System.out
								.printf("ResourceValidationFilter: doFilter - No Authorization Found - %s\n",
										e.toString());
					}
					
					// see if we have valid authorization and can get parameters
					if(authorization != null)
					{
						hasValidOAuthAccessToken = true;
						resourceUri = authorization.getResourceURI();
						subscription = authorization.getSubscription();
					}
				}
			}
		}
		
		// if this is not RESTful request with Bearer token
		if (hasBearer==false)
		{
			// no bearer token and it passed the OAuth filter - so it must be good2go not RESTAPI request
			invalid = false;
		}
		else if(hasValidOAuthAccessToken == true)
		{
			// if it has a valid access token 
			// then we know it is REST request
			// Dispatch on authentication type

			if ((invalid) && ((roles.contains("ROLE_USER")) || (roles.contains("ROLE_CUSTODIAN")))) {
				    System.out.printf("ResourceValidationFilter: role=%s\n", roles);
				
					// find the authorization
				    
					// strip the protocol://host:port
					resourceUri = resourceUri.substring(resourceUri.indexOf("/DataCustodian/"));
					if (uri.equals(resourceUri)) {
						invalid = false;
					}
			}
	
			if (invalid && roles.contains("ROLE_TP_ADMIN")) {
				//TODO
				System.out.printf("ResourceValidationFilter: ROLE_TP_ADMIN\n");
				invalid = false;
				
			}
			
			if (invalid && roles.contains("ROLE_DC_ADMIN")) {
				//TODO
				System.out.printf("ResourceValidationFilter: ROLE_DC_ADMIN\n");
				invalid = false;

			}
					
		    if (invalid && roles.contains("ROLE_ADMIN")) {
		    	//TODO
		    	System.out.printf("ResourceValidationFilter: ROLE_ADMIN\n");
	
		    }
		    
		    // if "ROLE_ANONYMOUS", or "ROLE_USER" These are the RESTful Resource APIs
		    //
			if (invalid && ((roles.contains("ROLE_ANONYMOUS")) || (roles.contains("ROLE_USER")))) {

				System.out.printf("ResourceValidationFilter: ROLE_ANONYMOUS || ROLE_USER: role=%s\n, Token - %s\n", roles, token);
	

				if (uri.contains("/espi/1_1/resource/Batch/Subscription/")) {
					// this is the normal access_token IF it matches the
					// resourceURI
					if (uri.equals(resourceUri))
						invalid = false;
				}

				if (invalid && uri.contains("/espi/1_1/resource/Batch/Bulk")) {
					// the Bulk Authorizations
				}

				if (uri.contains("/espi/1_1/resource/ApplicationInformation")) {
					// this will be a client_access_token
				}

				// look for the root forms of LocalTimeParameters and ReadingType
				// (These get a free pass for any valid access_token it would seem
				
				if (invalid && ((uri.contains("resource/LocalTimeParameters"))
						     || (uri.contains("resource/ReadingType")))) {
					invalid = false;
				}
				
				if (invalid && (uri.contains("/espi/1_1/resource/Authorization"))){
					// this is retrieving the authorization related to the token
					// with the special case that (maybe) client_access_token
					// will allow
					// access to the all

					String[] tokens = uri.split("/");
					Long authorizationId = 0L;
					Authorization requestedAuthorization = null;
					Long usagePointId = 0L;

					for (String s : tokens) {
						if (authorizationId == null)
							authorizationId = Long.parseLong(s, 10);
						if (s.equals("Authorization"))
							authorizationId = null;
					}

					if (authorizationId != null) {
						requestedAuthorization = authorizationService
								.findById(authorizationId);
						if (token.equals(requestedAuthorization
								.getAccessToken())) {
							invalid = false;
						}
					}

					// now need to test for the client_access_token to validate
					// access

					if (authorization.getResourceURI().contains(
							"ApplicationInformation")) {
						// then this is a client_access_token
						invalid = false;
					}

				}

				if (invalid
						&& uri.contains("/espi/1_1/resource/Batch/RetailCustomer")) {
					// this is the "upload_access_token"
					// TODO: need the resourceURI that goes with this one as
					// well
					// as the new multi-customer/multi-usagePoint pattern
					// currently it allows the access token from the authorization flow
					// and we just make sure the retail customer id is correct
					
					RetailCustomer retailCustomer = authorization.getRetailCustomer();
					Long requestRetailCustomerId = 0L;
					
					// there are two forms to consider
					
					String temp = uri;
					
					if (uri.contains("/UsagePoint")) {
						// "..resource/RetailCustomer/{retailCustomerId}/UsagePoint[/{usagePointId}]
						temp = temp.substring(temp.indexOf("Customer/") + "Customer/".length());
						temp = temp.substring(0, temp.indexOf("/"));
					} else {
						// "..resource/RetailCustomer/{retailCustomerId}
					    temp = temp.substring(temp.indexOf("RetailCustomer/") + "RetailCustomer/".length());
					}
					if (temp != null) 
						requestRetailCustomerId = new Long(temp);
					
					if (requestRetailCustomerId != 0L) {
						if (retailCustomer.getId().equals(requestRetailCustomerId)) invalid=false;
					} else {
						// it is a request for ALL retail customer usage points OR a Post
						// to inject a collection of UsagePoints (as yet undefined)
						// let it pass for now
						invalid = false;
					}
				}

				if (invalid && uri.contains("espi/1_1/resource/Subscription/")) {
					// this is a management_access_token or a normal
					// access_token
					try {
						// extract the subscriptionId and usagePointID (if any)
						// from
						// the uri
						Long subscriptionId = 0L;
						Subscription requestedSubscription = null;
						Long usagePointId = 0L;
						UsagePoint requestedUsagePoint = null;
						String[] tokens = uri.split("/");

						for (String s : tokens) {
							if (subscriptionId == null)
								subscriptionId = Long.parseLong(s, 10);
							if (usagePointId == null)
								usagePointId = Long.parseLong(s, 10);
							if (s.equals("Subscription"))
								subscriptionId = null;
							if (s.equals("UsagePoint"))
								usagePointId = null;
						}

						if (subscriptionId != null)
							requestedSubscription = subscriptionService
									.findById(subscriptionId);

						if (subscription.equals(requestedSubscription))
							if (usagePointId != null) {
								
							    // validate the usagePoint is appropriate
								requestedUsagePoint = usagePointService
										.findById(usagePointId);
								if (requestedUsagePoint != null) {
									// note that we need to iterate b/c .contains() fails to use
									// the class .equals().
									for (UsagePoint up : subscription.getUsagePoints()) {
										if (up.equals(requestedUsagePoint)) {
										    invalid = false;
										}
									}
								}
							} else {
								// it's good to go
								invalid = false;
							}

					} catch (Exception e) {
						System.out
								.printf("ResourceValidationFilter: doFilter - No Authorization Found - %s\n",
										e.toString());
					}
				}
			}				
		}

		
		System.out.printf("ResourceValidationFilter: doFilter: invalid=%s\n", invalid);
		
		if (invalid) return;
		
		chain.doFilter(req, res);

	}

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // nothing to do here
    }

}
