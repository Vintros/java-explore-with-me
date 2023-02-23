package ru.practicum.admin.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.common.model.Event;

@Repository
public interface AdmEventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    boolean existsByCategory_Id(Long id);

}
