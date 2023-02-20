package ru.practicum.admin.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.common.model.Compilation;

@Repository
public interface AdmCompilationRepository extends JpaRepository<Compilation, Long> {
}
