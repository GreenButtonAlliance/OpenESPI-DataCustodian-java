package org.energyos.espi.datacustodian.domain;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "third_parties")
@NamedQueries(value = {
    @NamedQuery(name = ThirdParty.QUERY_FIND_ALL, query = "SELECT third_party FROM ThirdParty third_party")
})
public class ThirdParty extends Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    public final static String QUERY_FIND_ALL = "ThirdParty.findAll";

    @Column(name = "name")
    @Size(min = 2, max = 64)
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
