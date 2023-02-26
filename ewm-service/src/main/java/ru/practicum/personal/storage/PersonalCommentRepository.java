package ru.practicum.personal.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.common.model.Comment;

import java.util.Optional;

@Repository
public interface PersonalCommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndEvent_Id(Long commentId, Long eventId);
}
