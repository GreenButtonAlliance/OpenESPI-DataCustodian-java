/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.energyos.espi.datacustodian.utils;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import org.apache.commons.lang.StringEscapeUtils;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

@Component
public class ATOMMarshaller {

    @Autowired
    private Jaxb2Marshaller marshaller;

    public FeedType unmarshal(InputStream stream) throws JAXBException {
        @SuppressWarnings("unchecked")
        JAXBElement<FeedType> object = (JAXBElement<FeedType>) marshaller.unmarshal(new StreamSource(stream));
        return object.getValue();
    }

    public String marshal(Feed feed) throws FeedException {
        return StringEscapeUtils.unescapeXml(new WireFeedOutput().outputString(feed));
    }
}
