package org.energyos.espi.datacustodian.service;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.energyos.espi.common.domain.Subscription;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void notify(Subscription subscription, XMLGregorianCalendar startDate, XMLGregorianCalendar endDate);
    
    void notify(List<Subscription> subscriptions, XMLGregorianCalendar startDate, XMLGregorianCalendar endDate);
    
}
