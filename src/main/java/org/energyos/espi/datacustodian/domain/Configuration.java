package org.energyos.espi.datacustodian.domain;

public class Configuration {
    public static final String DATA_CUSTODIAN_ID = "data_custodian";
    public static final String[] SCOPES = new String [] {
            "FB_4_5_15_IntervalDuration_3600_BlockDuration_monthly_HistoryLength_13",
            "FB_4_5_16_IntervalDuration_3600_BlockDuration_monthly_HistoryLength_13"
    };

    public static final String HTTP_WWW_W3_ORG_2005_ATOM = "http://www.w3.org/2005/Atom";
    public static final String HTTP_NAESB_ORG_ESPI = "http://naesb.org/espi";
    public static final String ATOM_PREFIX = "";
    public static final String ESPI_PREFIX = "espi";
}
