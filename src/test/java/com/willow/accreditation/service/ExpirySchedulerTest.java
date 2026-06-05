package com.willow.accreditation.service;

import com.willow.accreditation.model.Accreditation;
import com.willow.accreditation.util.AccreditationStatus;
import com.willow.accreditation.repository.InMemoryAccreditationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpirySchedulerTest {

    @Mock
    private InMemoryAccreditationRepository repository;

    @InjectMocks
    private ExpiryScheduler expiryScheduler;

    private Accreditation oldAccreditation;
    private Accreditation recentAccreditation;

    @BeforeEach
    void setUp() {
        oldAccreditation = new Accreditation();
        oldAccreditation.setId(UUID.randomUUID());
        oldAccreditation.setStatus(AccreditationStatus.CONFIRMED);
        oldAccreditation.setLastUpdatedAt(LocalDateTime.now().minusDays(31));

        recentAccreditation = new Accreditation();
        recentAccreditation.setId(UUID.randomUUID());
        recentAccreditation.setStatus(AccreditationStatus.CONFIRMED);
        recentAccreditation.setLastUpdatedAt(LocalDateTime.now().minusDays(15));
    }

    @Test
    void expireOldConfirmedAccreditations_ShouldExpireOldOnes() {
        when(repository.findByStatusAndLastUpdatedBefore(eq(AccreditationStatus.CONFIRMED), any(LocalDateTime.class)))
                .thenReturn(List.of(oldAccreditation));

        expiryScheduler.expireOldConfirmedAccreditations();

        verify(repository, times(1)).save(oldAccreditation);
        verify(repository, never()).save(recentAccreditation);
    }
}