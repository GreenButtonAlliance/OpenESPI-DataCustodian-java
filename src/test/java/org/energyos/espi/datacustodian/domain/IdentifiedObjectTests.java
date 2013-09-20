package org.energyos.espi.datacustodian.domain;

import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.annotation.XmlTransient;

import java.util.Set;

import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IdentifiedObjectTests {
    @Test
    public void isValid() throws Exception {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        IdentifiedObject identifiedObject = new IdentifiedObject();
        identifiedObject.setMRID("MRID");

        Set<ConstraintViolation<IdentifiedObject>> violations = validator.validate(identifiedObject);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void notValid() throws Exception {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        IdentifiedObject identifiedObject = new IdentifiedObject();

        Set<ConstraintViolation<IdentifiedObject>> violations = validator.validate(identifiedObject);

        assertFalse(violations.isEmpty());
    }

    @Test
    public void validations() {
        assertAnnotationPresent(IdentifiedObject.class, "mrid", NotEmpty.class);
    }

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
