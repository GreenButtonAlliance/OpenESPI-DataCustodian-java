package org.energyos.espi.datacustodian.web;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.energyos.espi.common.models.atom.DateTimeType;
import org.energyos.espi.common.models.atom.EntryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExportFilter {
    private Map<String, String> params;

    public ExportFilter(Map<String, String> params) {
        this.params = params;
    }

    public boolean matches(EntryType entry) {
        List<Boolean> matchers = new ArrayList<>();

        if (params.get("published-max") != null) {
            boolean publishedMax = toTime("published-max") >= toTime(entry.getPublished());
            matchers.add(publishedMax);
        }

        if (params.get("published-min") != null) {
            boolean publishedMin = toTime("published-min") <= toTime(entry.getPublished());
            matchers.add(publishedMin);
        }

        if (params.get("updated-max") != null) {
            boolean udpatedMax = toTime("updated-max") >= toTime(entry.getUpdated());
            matchers.add(udpatedMax);
        }

        if (params.get("updated-min") != null) {
            boolean updatedMin = toTime("updated-min") <= toTime(entry.getUpdated());
            matchers.add(updatedMin);
        }

        return !matchers.contains(false);
    }

    private long toTime(String key) {
        String param = params.get(key);

        return XMLGregorianCalendarImpl.parse(param).toGregorianCalendar().getTimeInMillis();
    }

    private long toTime(DateTimeType published) {
        return published.getValue().toGregorianCalendar().getTimeInMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExportFilter that = (ExportFilter) o;

        return params.equals(that.params);

    }

    @Override
    public int hashCode() {
        return params.hashCode();
    }
}
