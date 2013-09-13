package org.energyos.espi.datacustodian.domain;

import org.junit.Test;

import javax.xml.bind.annotation.XmlTransient;

import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;

public class IdentifiedObjectTests {

    @Test
    public void mrid_hasTransientAnnotation() {
        assertAnnotationPresent(IdentifiedObject.class, "mrid", XmlTransient.class);
    }

    @Test
    public void description_hasTransientAnnotation() {
        assertAnnotationPresent(IdentifiedObject.class, "description", XmlTransient.class);
    }

    @Test
    public void id_hasTransientAnnotation() {
        assertAnnotationPresent(IdentifiedObject.class, "id", XmlTransient.class);
    }
    @Test
    public void created_hasTransientAnnotation() {
        assertAnnotationPresent(IdentifiedObject.class, "created", XmlTransient.class);
    }

    @Test
    public void updated_hasTransientAnnotation() {
        assertAnnotationPresent(IdentifiedObject.class, "updated", XmlTransient.class);
    }
}
