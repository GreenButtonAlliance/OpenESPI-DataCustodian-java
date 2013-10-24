package org.energyos.espi.datacustodian.utils;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;

import java.util.HashMap;
import java.util.Map;

import static org.energyos.espi.datacustodian.domain.Configuration.*;

public class TestUtils {
    public static void setupXMLUnit() {

        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(namespaces()));
    }

    public static Map<String, String> namespaces() {
        Map<String, String> m = new HashMap<>();
        m.put(ATOM_PREFIX, HTTP_WWW_W3_ORG_2005_ATOM);
        m.put(ESPI_PREFIX, HTTP_NAESB_ORG_ESPI);
        return m;
    }
}
