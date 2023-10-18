package data.repository;

import data.dto.BoardDto;
import data.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {

    Page<BoardDto> findBoardByCategory(@Param("category") String category, Pageable pageable);

    Page<BoardEntity> findByCategoryAndSubjectContaining(String category, String keyword, Pageable pageable);
    Optional<BoardEntity> findByIdx(Long idx);

    void deleteAllByIdx(Long idx);

}
