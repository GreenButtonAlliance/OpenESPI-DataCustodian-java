/*
 * Copyright 2013 EnergyOS ESPI
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

package org.energyos.espi.datacustodian.models;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "usage_points")
@NamedQueries(value = {
        @NamedQuery(name = UsagePoint.QUERY_FIND_ALL_BY_RETAIL_CUSTOMER_ID,
                query = "SELECT point FROM UsagePoint point WHERE point.retailCustomer.id = :retailCustomerId")
})
public class UsagePoint extends BaseEntity {

    public static final String QUERY_FIND_ALL_BY_RETAIL_CUSTOMER_ID = "UsagePoint.findUsagePointsByRetailCustomer";

    @Column(name = "title")
    @NotEmpty @Size(max = 100)
    private String title;

    @ManyToOne
    @JoinColumn(name="retail_customer_id")
    private RetailCustomer retailCustomer;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RetailCustomer getRetailCustomer() {
        return retailCustomer;
    }

    public void setRetailCustomer(RetailCustomer retailCustomer) {
        this.retailCustomer = retailCustomer;
    }
}
