package org.energyos.espi.datacustodian.domain;

public class Configuration {
    public static Long DATA_CUSTODIAN_ID = 1L;
    public static String[] SCOPES = new String [] {
            "FB=5,15 IntervalDuration=60 BlockDuration=hourly HistoryLength=25",
            "FB=4,5,15 IntervalDuration=3600 BlockDuration=monthly HistoryLength=13",
            "FB=4,5,12,15,16 IntervalDuration=monthly BlockDuration=monthly HistoryLength=13"
    };
}
