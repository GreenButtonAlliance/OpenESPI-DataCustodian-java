package org.energyos.espi.datacustodian.domain;

import java.util.List;
public interface Linkable {
    void setUpResource(IdentifiedObject resource);

    String getRelatedLinkQuery();

    String getAllRelatedQuery();

    List<String> getRelatedLinkHrefs();
}
