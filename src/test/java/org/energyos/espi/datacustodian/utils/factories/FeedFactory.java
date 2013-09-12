package org.energyos.espi.datacustodian.utils.factories;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

import java.util.Date;

public class FeedFactory {
    private FeedFactory() {}

    public static Feed newFeed() {
        Feed feed = new Feed();
        feed.setFeedType("atom_1.0");
        feed.setId("urn:uuid:0071C5A7-91CF-434E-8BCE-C38AC8AF215D");
        feed.setTitle("Feed title");
        feed.setUpdated(new Date(113, 11, 28));

        Link self = newLink("self", "/ThirdParty/83e269c1/Batch");
        feed.getAlternateLinks().add(self);

        feed.getEntries().add(newUsagePointEntry());
        feed.getEntries().add(new Entry());
        return feed;
    }

    public static Entry newUsagePointEntry() {
        Entry entry = new Entry();
        entry.setId("urn:uuid:7BC41774-7190-4864-841C-861AC76D46C2");
        entry.setTitle("Front Electric Meter");
        entry.setPublished(new Date(112, 10, 15));
        entry.setUpdated(new Date(112, 10, 17));

        entry.getContents().add(newContent("<UsagePoint/>"));

        entry.getAlternateLinks().add(newLink("self", "RetailCustomer/9b6c7063/UsagePoint/01"));
        entry.getAlternateLinks().add(newLink("up", "RetailCustomer/9b6c7063/UsagePoint"));
        entry.getAlternateLinks().add(newLink("related", "RetailCustomer/9b6c7063/UsagePoint/01/MeterReading"));

        return entry;
    }

    public static Link newLink(String rel, String href) {
        Link entrySelf = new Link();
        entrySelf.setRel(rel);
        entrySelf.setHref(href);
        return entrySelf;
    }

    public static Content newContent(String value) {
        Content content = new Content();
        content.setValue(value);

        return content;
    }
}
