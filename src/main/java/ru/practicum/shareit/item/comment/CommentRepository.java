package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

   @Override
   @EntityGraph("comment.author")
   Optional<Comment> findById(Long commentId);

   List<Comment> findCommentsByItemId(Long authorId);
}
