package org.energyos.espi.datacustodian.domain;

import org.energyos.espi.datacustodian.models.atom.LinkType;

import java.util.List;

public interface Linkable {
    LinkType getUpLink();

    void setUpResource(IdentifiedObject resource);

    String getRelatedLinkQuery();

    String getAllRelatedQuery();

    List<String> getRelatedLinkHrefs();
}
