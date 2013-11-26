package org.energyos.espi.datacustodian.web.api;


import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.common.utils.EntryTypeIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class ExportService {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private Jaxb2Marshaller fragmentMarshaller;

    public void exportSubscription(String subscriptionHashedId, OutputStream stream) throws IOException {
        EntryTypeIterator entries = subscriptionService.findEntriesByHashedId(subscriptionHashedId);

        stream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
        stream.write("<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">".getBytes());

        StreamResult result = new StreamResult(stream);

        while (entries.hasNext()) {
            fragmentMarshaller.marshal(entries.next(), result);
        }

        stream.write("</feed>".getBytes());
    }

    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void setMarshaller(Jaxb2Marshaller fragmentMarshaller) {
        this.fragmentMarshaller = fragmentMarshaller;
    }
}
