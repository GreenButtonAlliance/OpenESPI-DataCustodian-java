package org.energyos.espi.datacustodian.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "third_parties")
@NamedQueries(value = {
        @NamedQuery(name = ThirdParty.QUERY_FIND_BY_ID, query = "SELECT third_party FROM ThirdParty third_party WHERE third_party.id = :id"),
        @NamedQuery(name = ThirdParty.QUERY_FIND_ALL, query = "SELECT third_party FROM ThirdParty third_party")
})
public class ThirdParty extends Resource {

    public final static String QUERY_FIND_ALL = "ThirdParty.findAll";
    public static final String QUERY_FIND_BY_ID = "ThirdParty.findById";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "name")
    @NotEmpty @Size(min = 2, max = 64)
    protected String name;

    @Column(name = "url")
    protected String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
