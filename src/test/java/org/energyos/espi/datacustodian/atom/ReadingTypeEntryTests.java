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

package org.energyos.espi.datacustodian.atom;


import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.domain.RationalNumber;
import org.energyos.espi.datacustodian.domain.ReadingInterharmonic;
import org.energyos.espi.datacustodian.domain.ReadingType;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.math.BigInteger;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.Asserts.assertXpathValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReadingTypeEntryTests {

    private ReadingType readingType;

    @Before
    public void before() {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);

        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setId(1L);
        usagePoint.setDescription("Electric Meter");
        readingType = newReadingType();
    }

    @Test
    public void constructsReadingTypeEntry() throws FeedException, SAXException, IOException, XpathException {


        ReadingTypeEntry entry = new ReadingTypeEntry(readingType);
        assertNotNull("entry was null", entry);

        assertEquals("ReadingType/8", entry.getSelfLink().getHref());
        assertEquals("ReadingType", entry.getUpLink().getHref());

        Content content = (Content)entry.getContents().get(0);
        String xmlContent = content.getValue();

        assertXpathExists("ReadingType", xmlContent);
        assertXpathValue("accumulationBehaviour", "ReadingType/accumulationBehaviour", xmlContent);
        assertXpathValue("commodity", "ReadingType/commodity", xmlContent);
        assertXpathValue("dataQualifier", "ReadingType/dataQualifier", xmlContent);
        assertXpathValue("10", "ReadingType/intervalLength", xmlContent);
        assertXpathValue("kind", "ReadingType/kind", xmlContent);
        assertXpathValue("phase", "ReadingType/phase", xmlContent);
        assertXpathValue("multiplier", "ReadingType/powerOfTenMultiplier", xmlContent);
        assertXpathValue("uom", "ReadingType/uom", xmlContent);
        assertXpathValue("currency", "ReadingType/currency", xmlContent);
        assertXpathValue("tou", "ReadingType/tou", xmlContent);
        assertXpathValue("aggregate", "ReadingType/aggregate", xmlContent);
        assertXpathValue("1", "ReadingType/argument/numerator", xmlContent);
        assertXpathValue("3", "ReadingType/argument/denominator", xmlContent);
        assertXpathValue("1", "ReadingType/interharmonic/numerator", xmlContent);
        assertXpathValue("6", "ReadingType/interharmonic/denominator", xmlContent);
    }

    private ReadingType newReadingType() {
        ReadingType type = new ReadingType();
        RationalNumber argument = new RationalNumber();
        argument.setNumerator(new BigInteger("1"));
        argument.setDenominator(new BigInteger("3"));
        ReadingInterharmonic interharmonic = new ReadingInterharmonic();
        interharmonic.setNumerator(new BigInteger("1"));
        interharmonic.setDenominator(new BigInteger("6"));

        type.setId(8L);
        type.setDescription("Energy Delivered");
        type.setAccumulationBehaviour("accumulationBehaviour");
        type.setCommodity("commodity");
        type.setDataQualifier("dataQualifier");
        type.setIntervalLength(10L);
        type.setKind("kind");
        type.setPhase("phase");
        type.setPowerOfTenMultiplier("multiplier");
        type.setUom("uom");
        type.setCurrency("currency");
        type.setTou("tou");
        type.setAggregate("aggregate");
        type.setArgument(argument);
        type.setInterharmonic(interharmonic);

        return type;
    }
}