package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.datacustodian.models.UsagePoint;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;

@Component
public class UsagePointUnmarshaller {

    public UsagePoint unmarshal(InputStream stream) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(UsagePoint.class);
        return (UsagePoint)context.createUnmarshaller().unmarshal(stream);
    }
}
