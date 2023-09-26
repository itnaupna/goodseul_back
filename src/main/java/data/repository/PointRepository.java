package data.repository;


import data.entity.PointEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<PointEntity, Integer> {
    Page<PointEntity> findByMemberIdx(int memberIdx, Pageable pageable);

}
