package ru.practicum.admin.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.common.model.Category;

@Repository
public interface AdmCategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
