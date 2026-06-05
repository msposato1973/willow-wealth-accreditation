package com.willow.accreditation.repository;

import com.willow.accreditation.model.Accreditation;
import com.willow.accreditation.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAccreditationRepositoryTest {

    private InMemoryAccreditationRepository repository;
    private Accreditation accreditation;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAccreditationRepository();

        accreditation = new Accreditation();
        accreditation.setUserId("user-123");
        accreditation.setType(AccreditationType.BY_INCOME);
        accreditation.setStatus(AccreditationStatus.PENDING);
    }

    @Test
    void save_ShouldStoreAccreditation() {
        repository.save(accreditation);

        Optional<Accreditation> found = repository.findById(accreditation.getId());
        assertTrue(found.isPresent());
        assertEquals(accreditation.getUserId(), found.get().getUserId());
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotFound() {
        Optional<Accreditation> found = repository.findById(UUID.randomUUID());
        assertFalse(found.isPresent());
    }

    @Test
    void findByUserId_ShouldReturnUserAccreditations() {
        repository.save(accreditation);

        List<Accreditation> userAccreditations = repository.findByUserId("user-123");
        assertEquals(1, userAccreditations.size());
    }

    @Test
    void existsPendingForUser_ShouldReturnTrueWhenPendingExists() {
        repository.save(accreditation);

        boolean exists = repository.existsPendingForUser("user-123");
        assertTrue(exists);
    }

    @Test
    void existsPendingForUser_ShouldReturnFalseWhenNoPending() {
        accreditation.setStatus(AccreditationStatus.CONFIRMED);
        repository.save(accreditation);

        boolean exists = repository.existsPendingForUser("user-123");
        assertFalse(exists);
    }

    @Test
    void findByStatusAndLastUpdatedBefore_ShouldReturnOldAccreditations() {
        accreditation.setStatus(AccreditationStatus.CONFIRMED);
        accreditation.setLastUpdatedAt(LocalDateTime.now().minusDays(31));
        repository.save(accreditation);

        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<Accreditation> oldAccreditations = repository.findByStatusAndLastUpdatedBefore(
                AccreditationStatus.CONFIRMED, cutoff
        );

        assertEquals(1, oldAccreditations.size());
    }
}