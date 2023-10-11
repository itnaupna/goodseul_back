package data.repository;

import data.dto.BoardDto;
import data.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {

    @Query("SELECT new data.dto.BoardDto(b.subject, b.tag, b.writeDate) FROM BoardEntity b WHERE b.category = :category")
    Page<BoardDto> findBoardByCategory(@Param("category") String category, Pageable pageable);
}
