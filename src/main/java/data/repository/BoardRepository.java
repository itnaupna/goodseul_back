package data.repository;

import data.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {

    Page<BoardEntity> findByCategoryAndSubjectContaining(String category, String keyword, Pageable pageable);
    Optional<BoardEntity> findByIdx(Long idx);
    void deleteAllByIdx(Long idx);

}
