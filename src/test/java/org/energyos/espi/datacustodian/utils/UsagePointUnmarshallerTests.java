package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.datacustodian.models.UsagePoint;
import org.hsqldb.lib.StringInputStream;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

import static junit.framework.Assert.assertEquals;

public class UsagePointUnmarshallerTests {

    @Test(expected = javax.xml.bind.UnmarshalException.class)
    public void givenInvalidInput_throwsAnException() throws JAXBException {
        UsagePointUnmarshaller unmarshaller  = new UsagePointUnmarshaller();

        unmarshaller.unmarshal(new StringInputStream(""));
    }

    @Test
    public void givenValidInput_returnsUsagePoint() throws JAXBException, FileNotFoundException {
        String XML =  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<UsagePoint><title>Electric meter</title></UsagePoint>";
        UsagePointUnmarshaller unmarshaller  = new UsagePointUnmarshaller();
        ByteArrayInputStream input = new ByteArrayInputStream(XML.getBytes());

        UsagePoint usagePoint = unmarshaller.unmarshal(input);

        assertEquals("Electric meter", usagePoint.getTitle());
    }
}
