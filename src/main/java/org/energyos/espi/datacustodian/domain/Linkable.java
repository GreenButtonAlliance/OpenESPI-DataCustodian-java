package org.energyos.espi.datacustodian.domain;

public interface Linkable {
    void setUpResource(IdentifiedObject resource);

    String getRelatedLinkQuery();
}
