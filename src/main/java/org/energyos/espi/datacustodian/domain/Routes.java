package org.energyos.espi.datacustodian.domain;

public class Routes {
    public static final String DataCustodianHome = "/custodian/home";
    public static final String DataCustodianNotifyThirdParty = "/DataCustodian/NotifyThirdParty";
    public static final String DataCustodianRemoveAllOAuthTokens = "/custodian/removealltokens";
    public static final String DataCustodianSubscription = "/espi/1_1/resource/Subscription/{SubscriptionID}";
    public static final String DataCustodianAuthorization = "/espi/1_1/resource/Authorization/{AuthorizationID}";

    public static final String CUSTODIAN_RETAILCUSTOMERS_USAGEPOINTS_FORM = "/custodian/retailcustomers/usagepoints/form";
    public static final String CUSTODIAN_RETAILCUSTOMERS = "/custodian/retailcustomers";

    public static final String DataCustodianRESTUsagePointCollection = "/espi/1_1/resource/RetailCustomer/{retailCustomerId}/UsagePoint";
    public static final String DataCustodianRESTUsagePointCreate = "/espi/1_1/resource/RetailCustomer/{retailCustomerId}/UsagePoint";
    public static final String DataCustodianRESTUsagePointMember = "/espi/1_1/resource/RetailCustomer/{retailCustomerHashedId}/UsagePoint/{usagePointHashedId}";
    public static final String DataCustodianRESTUsagePointUpdate = "/espi/1_1/resource/RetailCustomer/{retailCustomerHashedId}/UsagePoint/{usagePointHashedId}";

    public static String newDataCustodianRESTUsagePointCollection(String retailCustomerHashedId) {
        return DataCustodianRESTUsagePointCollection.replace("{retailCustomerId}", retailCustomerHashedId);
    }

    public static String newDataCustodianRESTUsagePointMember(String retailCustomerHashedId, String usagePointHashedId) {
        return DataCustodianRESTUsagePointMember.replace("{retailCustomerHashedId}", retailCustomerHashedId).replace("{usagePointHashedId}", usagePointHashedId);
    }
}
