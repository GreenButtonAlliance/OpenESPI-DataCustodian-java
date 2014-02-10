package org.energyos.espi.datacustodian.web;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.energyos.espi.common.models.atom.EntryType;
import org.energyos.espi.common.utils.DateConverter;
import org.energyos.espi.common.utils.ExportFilter;
import org.junit.Before;
import org.junit.Test;

public class ExportFilterTests {

    private Map<String,String> params;
    private ExportFilter exportFilter;

    @Before
    public void setUp() {
        params = new HashMap<>();
    }

    @Test
    public void matches() throws Exception {
        exportFilter = new ExportFilter(params);
        EntryType entry = new EntryType();
        assertThat(exportFilter.matches(entry), is(true));
    }

    @Test
    public void matches_givenPublishedMin() throws Exception {
        params.put("published-min", getXMLTime(51));
        exportFilter = new ExportFilter(params);
        assertThat(exportFilter.matches(getEntry(50)), is(false));
        assertThat(exportFilter.matches(getEntry(52)), is(true));
    }

    @Test
    public void matches_givenPublishedMax() throws Exception {
        params.put("published-max", getXMLTime(51));
        exportFilter = new ExportFilter(params);
        assertThat(exportFilter.matches(getEntry(50)), is(true));
        assertThat(exportFilter.matches(getEntry(52)), is(false));
    }

    @Test
    public void matches_givenUpdatedMin() throws Exception {
        params.put("updated-min", getXMLTime(51));
        exportFilter = new ExportFilter(params);
        assertThat(exportFilter.matches(getEntry(50)), is(false));
        assertThat(exportFilter.matches(getEntry(52)), is(true));
    }

    @Test
    public void matches_givenUpdatedMax() throws Exception {
        params.put("updated-max", getXMLTime(51));
        exportFilter = new ExportFilter(params);
        assertThat(exportFilter.matches(getEntry(50)), is(true));
        assertThat(exportFilter.matches(getEntry(52)), is(false));
    }

    @Test
    public void matches_givenMaxResults() throws Exception {
        params.put("max-results", "1");
        exportFilter = new ExportFilter(params);

        assertThat(exportFilter.matches(getEntry(0)), is(true));
        assertThat(exportFilter.matches(getEntry(0)), is(false));
    }

    @Test
    public void matches_givenStartIndex_zeroBased() throws Exception {
        params.put("start-index", "1");
        exportFilter = new ExportFilter(params);

        assertThat(exportFilter.matches(getEntry(0)), is(true));
        assertThat(exportFilter.matches(getEntry(0)), is(true));
        assertThat(exportFilter.matches(getEntry(0)), is(true));
    }

    @Test
    public void matches_givenStartIndex_andMaxResults() throws Exception {
        params.put("start-index", "1");
        params.put("max-results", "1");

        exportFilter = new ExportFilter(params);

        assertThat(exportFilter.matches(getEntry(0)), is(true));
        assertThat(exportFilter.matches(getEntry(0)), is(false));
        assertThat(exportFilter.matches(getEntry(0)), is(false));
    }

    @Test
    public void matches_givenStartIndex_andMaxResults_andPublishedRange() throws Exception {
        params.put("start-index", "1");
        params.put("max-results", "2");
        params.put("published-min", getXMLTime(50));
        params.put("published-max", getXMLTime(50));


        exportFilter = new ExportFilter(params);

        assertThat(exportFilter.matches(getEntry(0)), is(false));
        assertThat(exportFilter.matches(getEntry(50)), is(true));
        assertThat(exportFilter.matches(getEntry(50)), is(true));
        assertThat(exportFilter.matches(getEntry(0)), is(false));
        assertThat(exportFilter.matches(getEntry(50)), is(false));
        assertThat(exportFilter.matches(getEntry(50)), is(false));
    }

    private EntryType getEntry(int secondsFromEpoch) {
        EntryType entry = new EntryType();
        entry.setPublished(DateConverter.toDateTimeType(getGregorianCalendar(secondsFromEpoch)));
        entry.setUpdated(DateConverter.toDateTimeType(getGregorianCalendar(secondsFromEpoch)));
        return entry;
    }

    private GregorianCalendar getGregorianCalendar(int secondsFromEpoch) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(secondsFromEpoch * 1000);
        return cal;
    }

    private String getXMLTime(int millis) throws DatatypeConfigurationException {
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        GregorianCalendar cal = getGregorianCalendar(millis);
        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(cal);
        xmlGregorianCalendar.setFractionalSecond(null);
        return xmlGregorianCalendar.toXMLFormat();
    }
}
