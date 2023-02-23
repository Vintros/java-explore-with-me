package ru.practicum.admin.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.common.model.Location;

@Repository
public interface AdmLocationRepository extends JpaRepository<Location, Long> {
}
