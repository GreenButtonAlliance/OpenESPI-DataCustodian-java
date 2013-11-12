package org.energyos.espi.datacustodian.models.atom;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class EntryTypeTests {
    @Test
    public void addRelatedLink() throws Exception {
        EntryType entry = new EntryType();

        entry.addRelatedLink("href");

        assertThat(entry.getLinks().get(0).getHref(), equalTo("href"));
        assertThat(entry.getLinks().get(0).getRel(), equalTo("related"));
    }

    @Test
    public void addUpLink() throws Exception {
        EntryType entry = new EntryType();

        entry.addUpLink("href");

        assertThat(entry.getLinks().get(0).getHref(), equalTo("href"));
        assertThat(entry.getLinks().get(0).getRel(), equalTo("up"));
    }

    @Test
    public void getUpHref() throws Exception {
        EntryType entry = new EntryType();

        entry.addUpLink("href");

        assertThat(entry.getUpHref(), equalTo("href"));
    }
}
