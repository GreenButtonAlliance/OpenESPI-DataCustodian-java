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

package org.energyos.espi.datacustodian.console;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.utils.ATOMMarshaller;
import org.energyos.espi.datacustodian.utils.UsagePointBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileInputStream;

public class ImportUsagePoint {
    private static UsagePointService usagePointService;

    public static void main(String[] args) {
        if (args.length > 0) {
            String filename = args[0];
            ApplicationContext springContext = new ClassPathXmlApplicationContext("classpath:spring/business-config.xml");
            UsagePointBuilder builder = springContext.getBean(UsagePointBuilder.class);
            ATOMMarshaller marshaller = springContext.getBean(ATOMMarshaller.class);
            usagePointService = springContext.getBean(UsagePointService.class);

            try {
                persistUsagePoint(builder.newUsagePoint(marshaller.unmarshal(new FileInputStream(filename))));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println("**********");
            System.out.println("\nXML file imported successfully.\n");
            System.out.println("**********");
        } else {
            System.out.println("**********");
            System.out.println("\nPlease specify an XML file to import.\n");
            System.out.println("**********");
            System.exit(1);
        }
    }

    public static void persistUsagePoint(UsagePoint up) {
        RetailCustomer customer = new RetailCustomer();
        customer.setFirstName("Alan");
        customer.setLastName("Turing");
        customer.setId(1L);

        up.setRetailCustomer(customer);

        usagePointService.persist(up);
    }

    public static void setUsagePointService(UsagePointService usagePointService) {
        ImportUsagePoint.usagePointService = usagePointService;
    }
}
