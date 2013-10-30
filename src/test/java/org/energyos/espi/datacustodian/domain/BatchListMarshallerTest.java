package org.energyos.espi.datacustodian.domain;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class BatchListMarshallerTest extends XMLTest {
    @Autowired
    @Qualifier(value = "domainMarshaller")
    Jaxb2Marshaller jaxb2Marshaller;

    public String newXML() {
        BatchList batchList = new BatchList();
        List<String> list = new LinkedList<>();
        list.add("foo");
        list.add("bar");
        batchList.setResources(list);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        jaxb2Marshaller.marshal(batchList, new StreamResult(os));
        return os.toString();
    }

    @Test
    public void batchList() throws SAXException, IOException, XpathException {
        System.out.println(newXML());
        assertXpathExists("/espi:BatchList", newXML());
    }
    @Test
    public void resources() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("foo", "/espi:BatchList/espi:resources[1]", newXML());
        assertXpathEvaluatesTo("bar", "/espi:BatchList/espi:resources[2]", newXML());
    }
}
