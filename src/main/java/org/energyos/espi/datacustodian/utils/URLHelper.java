package org.energyos.espi.datacustodian.utils;

import java.util.Set;

public class URLHelper {

    public static String newScopeParams(String[] scopes) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < scopes.length; i++) {
            if(i > 0)
                sb.append("&");
            sb.append("scope=" + scopes[i]);
        }
        return sb.toString();
    }

    public static String newScopeParams(Set<String> scopes) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(String scope : scopes) {
            if(i > 0)
                sb.append("&");
            sb.append("scope=" + scope);
            i++;
        }
        return sb.toString();
    }
}
