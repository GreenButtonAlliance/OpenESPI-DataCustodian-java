package org.energyos.espi.datacustodian.domain;

public class Routes {
    public static final String DataCustodianHome = "/custodian/home";
    public static final String DataCustodianRemoveAllOAuthTokens = "/custodian/removealltokens";
    public static final String DataCustodianSubscription = "/espi/1_1/resource/Subscription/{SubscriptionID}";
    public static final String DataCustodianAuthorization = "/espi/1_1/resource/Authorization/{AuthorizationID}";

    public static final String CUSTODIAN_RETAILCUSTOMERS_USAGEPOINTS_FORM = "/custodian/retailcustomers/usagepoints/form";
    public static final String CUSTODIAN_RETAILCUSTOMERS = "/custodian/retailcustomers";
}
