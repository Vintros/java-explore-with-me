package ru.practicum.personal.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.common.model.Event;


import java.util.List;

@Repository
public interface PersonalEventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);
}
