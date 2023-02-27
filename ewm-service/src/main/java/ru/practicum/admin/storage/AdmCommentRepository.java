package ru.practicum.admin.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.common.model.Comment;

@Repository
public interface AdmCommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    boolean existsByIdAndEvent_Id(Long commentId, Long eventId);
}
