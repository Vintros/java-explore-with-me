package ru.practicum.personal.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.common.model.EventRequest;
import ru.practicum.common.util.StateRequest;

import java.util.List;

@Repository
public interface PersonalRequestRepository extends JpaRepository<EventRequest, Long> {

    List<EventRequest> findAllByRequester_Id(Long userId);

    List<EventRequest> findAllByIdIn(List<Long> requestId);

    boolean existsByEvent_idAndRequester_IdAndStatusIs(Long eventId, Long userId, StateRequest state);

}
