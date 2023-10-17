package data.repository;

import data.entity.BoardEntity;
import data.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Optional<CommentEntity> findByBoards(BoardEntity boards);
}