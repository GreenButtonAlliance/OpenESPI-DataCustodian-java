package org.energyos.espi.datacustodian.web.custodian;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.energyos.espi.common.domain.ApplicationInformation;
import org.energyos.espi.common.domain.Authorization;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.ServiceCategory;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.NotificationService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.common.service.UsagePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class AssociateUsagePointController {

    @Autowired
    private RetailCustomerService retailCustomerService;
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    UsagePointService service;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new UsagePointFormValidator());
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_RETAIL_CUSTOMER_USAGE_POINTS_FORM, method = RequestMethod.GET)
    public String form(@PathVariable Long retailCustomerId, ModelMap model) {
        model.put("usagePointForm", new UsagePointForm());
        model.put("retailCustomerId", retailCustomerId);

        return "/custodian/retailcustomers/usagepoints/form";
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_RETAIL_CUSTOMER_USAGE_POINTS_CREATE, method = RequestMethod.POST)
    public String create(@PathVariable Long retailCustomerId, @ModelAttribute("usagePointForm") @Valid UsagePointForm usagePointForm, BindingResult result) {
        if (result.hasErrors())
            return "/custodian/retailcustomers/usagepoints/form";
        
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setUUID(UUID.fromString(usagePointForm.getUUID()));
        usagePoint.setDescription(usagePointForm.getDescription());

        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        usagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.ELECTRICITY_SERVICE));
        usagePoint.setRetailCustomer(retailCustomer);
        service.createOrReplaceByUUID(usagePoint);
        
        // now see if there are any authorizations for this information
        //
        try {
        
        	List<Authorization> authorizationList = authorizationService.findAllByRetailCustomerId(retailCustomer.getId());
        	Iterator<Authorization> authorizationIterator = authorizationList.iterator();
        
        	while (authorizationIterator.hasNext()) {
        	
        		Authorization authorization = authorizationIterator.next();
        		Subscription subscription = subscriptionService.findByAuthorizationId(authorization.getId()); 
        		String resourceUri = authorization.getResourceURI();
        		if (resourceUri == null) {
			
        			// this is the first time this authorization has been in effect. We
        			// must set up the appropriate resource links
        			ApplicationInformation applicationInformation = authorization.getApplicationInformation();
        			resourceUri = applicationInformation.getDataCustodianResourceEndpoint();
        			resourceUri = resourceUri + "/Batch/Subscription/" + subscription.getId();	
        			authorization.setResourceURI(resourceUri);
        		}

        		// make sure the UsagePoints we just imported are linked up with
        		//  the subscription if any
        		subscription.getUsagePoints().add(usagePoint);
        		subscription = subscriptionService.addUsagePoint(subscription, usagePoint);
        	}      
        
        	// now do any notifications
        	notificationService.notify(retailCustomer, null, null);
        } catch (Exception e) {
        	
        }
        return "redirect:/custodian/retailcustomers";
    }

    public void setRetailCustomerService(RetailCustomerService retailCustomerService) {
        this.retailCustomerService = retailCustomerService;
    }

    public void setService(UsagePointService service) {
        this.service = service;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
    
    public static class UsagePointForm {
        private String uuid;
        private String description;

        public String getUUID() {
            return uuid;
        }

        public void setUUID(String uuid) {
            this.uuid = uuid;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class UsagePointFormValidator implements Validator {

        public boolean supports(Class clazz) {
            return UsagePointForm.class.isAssignableFrom(clazz);
        }

        public void validate(Object target, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "UUID", "field.required", "UUID is required");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "field.required", "Description is required");
            try {
                UsagePointForm form = (UsagePointForm)target;
                UUID.fromString(form.getUUID());
            } catch(IllegalArgumentException x) {
                errors.rejectValue("UUID", "uuid.required", null, "Must be a valid UUID Ex. 550e8400-e29b-41d4-a716-446655440000");
            }
        }
    }
}
