package org.energyos.espi.datacustodian.domain;

import java.util.ArrayList;
import java.util.List;

public class BatchList {
    private List<String> resources = new ArrayList<>();

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }
}
