/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.energyos.espi.datacustodian.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "retail_customers")
@NamedQueries(value = {
        @NamedQuery(name = RetailCustomer.QUERY_FIND_ALL, query = "SELECT customer FROM RetailCustomer customer")
})
public class RetailCustomer extends IdentifiedObject {

    public final static String QUERY_FIND_ALL = "RetailCustomer.findAll";

    @Column(name = "first_name")
    @NotEmpty @Size(max = 30)
    protected String firstName;

    @Column(name = "last_name")
    @NotEmpty @Size(max = 30)
    protected String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
