package org.energyos.espi.datacustodian.atom;


import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;

import java.util.HashMap;
import java.util.Map;

import static org.energyos.espi.datacustodian.domain.Configuration.*;

public class XMLTest {

    @BeforeClass
    public static void beforeClass() {
        Map<String, String> m = new HashMap<>();
        m.put(ATOM_PREFIX, HTTP_WWW_W3_ORG_2005_ATOM);
        m.put(ESPI_PREFIX, HTTP_NAESB_ORG_ESPI);
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(m));
    }
}
