package org.energyos.espi.datacustodian.atom;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.IntervalBlock;
import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.utils.EspiMarshaller;

import java.util.ArrayList;
import java.util.List;

public class IntervalBlocksEntry extends EspiEntry {

    private IntervalBlock content;

    public IntervalBlocksEntry(List<IntervalBlock> intervalBlocks) throws FeedException {
        super(intervalBlocks.get(0));

        this.content = intervalBlocks.get(0);
        selfLink = buildSelfLink();
        upLink = buildUpLink();

        this.setContents(buildContents(intervalBlocks));
    }

    private List<Content> buildContents(List<IntervalBlock> intervalBlocks) throws FeedException {
        Content content = new Content();
        List<Content> contents = new ArrayList<>();
        StringBuilder intervalBlocksXml = new StringBuilder(40);

        for (IntervalBlock intervalBlock : intervalBlocks) {
            intervalBlocksXml.append(EspiMarshaller.marshal(intervalBlock));
            intervalBlocksXml.append("\n");
        }

        content.setValue(intervalBlocksXml.toString());
        contents.add(content);

        return contents;
    }

    private Link buildSelfLink() {
        Link link = new Link();

        link.setRel("self");
        link.setHref(getSelfHref());

        return link;
    }

    private String getSelfHref() {
        MeterReading meterReading = content.getMeterReading();
        UsagePoint usagePoint = meterReading.getUsagePoint();

        return "RetailCustomer/" + usagePoint.getRetailCustomer().getId() +
                "/UsagePoint/" + usagePoint.getId() +
                "/MeterReading/" + meterReading.getId() +
                "/IntervalBlock/" + content.getId();
    }

    private Link buildUpLink() {
        Link link = new Link();

        link.setRel("up");
        link.setHref(getUpHref());

        return link;
    }

    private String getUpHref() {
        MeterReading meterReading = content.getMeterReading();
        UsagePoint usagePoint = meterReading.getUsagePoint();

        return "RetailCustomer/" + usagePoint.getRetailCustomer().getId() +
                "/UsagePoint/" + usagePoint.getId() +
                "/MeterReading/" + meterReading.getId() +
                "/IntervalBlock";
    }
}
