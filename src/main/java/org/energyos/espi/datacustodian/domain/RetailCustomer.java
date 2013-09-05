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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "retail_customers")
@NamedQueries(value = {
        @NamedQuery(name = RetailCustomer.QUERY_FIND_ALL, query = "SELECT customer FROM RetailCustomer customer"),
        @NamedQuery(name = RetailCustomer.QUERY_FIND_BY_USERNAME, query = "SELECT customer FROM RetailCustomer customer WHERE customer.username = :username")
})
public class RetailCustomer extends IdentifiedObject implements UserDetails, Principal {

    public final static String QUERY_FIND_ALL = "RetailCustomer.findAll";
    public final static String QUERY_FIND_BY_USERNAME = "RetailCustomer.findByUsername";
    public final static String ROLE_CUSTOMER  = "ROLE_CUSTOMER";
    public final static String ROLE_CUSTODIAN  = "ROLE_CUSTODIAN";


    @Column(name = "username")
    @Size(min = 4, max = 30)
    protected String username;

    @Column(name = "first_name")
    @NotEmpty @Size(max = 30)
    protected String firstName;

    @Column(name = "last_name")
    @NotEmpty @Size(max = 30)
    protected String lastName;

    @Column(name = "password")
    @Size(min = 5, max = 100)
    protected String password;

    @Column(name = "enabled")
    @NotNull
    protected Boolean enabled = Boolean.TRUE;

    @Column(name = "role")
    @NotEmpty
    protected String role = ROLE_CUSTOMER;

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String getName() {
        return getUsername();
    }
}
