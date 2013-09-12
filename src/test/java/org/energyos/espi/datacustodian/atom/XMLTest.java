package org.energyos.espi.datacustodian.atom;


import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;

public class XMLTest {

    @BeforeClass
    public static void beforeClass() {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);
    }
}
