package com.willow.accreditation.repository;

import com.willow.accreditation.model.Accreditation;
import com.willow.accreditation.util.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryAccreditationRepository {

    private final Map<UUID, Accreditation> storage = new ConcurrentHashMap<>();

    public void save(Accreditation accreditation) {
        storage.put(accreditation.getId(), accreditation);
    }

    public Optional<Accreditation> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Accreditation> findByUserId(String userId) {
        return storage.values().stream()
                .filter(acc -> acc.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public boolean existsPendingForUser(String userId) {
        return storage.values().stream()
                .anyMatch(acc -> acc.getUserId().equals(userId) &&
                        acc.getStatus() == AccreditationStatus.PENDING);
    }

    public List<Accreditation> findByStatusAndLastUpdatedBefore(AccreditationStatus status, LocalDateTime dateTime) {
        return storage.values().stream()
                .filter(acc -> acc.getStatus() == status)
                .filter(acc -> acc.getLastUpdatedAt().isBefore(dateTime))
                .collect(Collectors.toList());
    }

    public List<Accreditation> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(UUID id) {
        storage.remove(id);
    }

    public void clear() {
        storage.clear();
    }

    public long count() {
        return storage.size();
    }
}