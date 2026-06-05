package com.willow.accreditation.service;

import com.willow.accreditation.model.Accreditation;
import com.willow.accreditation.model.*;
import com.willow.accreditation.repository.InMemoryAccreditationRepository;
import com.willow.accreditation.util.AccreditationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ExpiryScheduler {

    @Autowired
    private InMemoryAccreditationRepository repository;

    @Value("${scheduler.expiry.days:30}")
    private int expiryDays;

    @Scheduled(fixedRateString = "${scheduler.expiry.fixed-rate:3600000}")
    public void expireOldConfirmedAccreditations() {
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(expiryDays);

        List<Accreditation> oldConfirmedAccreditations = repository.findByStatusAndLastUpdatedBefore(
                AccreditationStatus.CONFIRMED, expiryDate
        );

        for (Accreditation accreditation : oldConfirmedAccreditations) {
            accreditation.setStatus(AccreditationStatus.EXPIRED);
            accreditation.setLastUpdatedAt(LocalDateTime.now());
            repository.save(accreditation);
        }

        if (!oldConfirmedAccreditations.isEmpty()) {
            System.out.println("[" + LocalDateTime.now() + "] Expired " +
                    oldConfirmedAccreditations.size() +
                    " accreditations older than " + expiryDays + " days");
        }
    }
}