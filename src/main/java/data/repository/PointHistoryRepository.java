package data.repository;


import data.entity.PointEntity;
import data.entity.PointHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Integer> {
    List<PointHistoryEntity> findByPointEntity_PointIdx(int pointIdx);
    List<PointHistoryEntity> findByOriginIdx(Integer originIdx);
    boolean existsByOriginIdxAndType(Integer originIdx, String type);
    @Query("SELECT ph.pointEntity.pointIdx, SUM(ph.point), ph.expireDate FROM point_history ph WHERE ph.memberIdx = :memberIdx AND ph.expireDate > NOW()" +
            "GROUP BY ph.pointEntity.pointIdx HAVING SUM(ph.point) != 0 ORDER BY ph.expireDate, ph.pointEntity.pointIdx asc")
    List<Object[]> findNonZeroGroupedByPointId(@Param("memberIdx") int memberIdx);

    @Query("SELECT SUM(ph.point) FROM point_history ph WHERE ph.memberIdx = :memberName AND ph.expireDate > Now() GROUP BY ph.memberIdx")
    Integer findTotalPointsByMemberName(@Param("memberName") int memberIdx);

    @Query("SELECT ph, SUM(ph.point) FROM point_history ph WHERE ph.expireDate < NOW() GROUP BY ph.pointEntity.pointIdx HAVING SUM(ph.point) != 0")
    List<Object[]> findAllExpiredPoints();

    @Query("SELECT ph FROM point_history ph WHERE ph.expireDate < now() AND ph.type = '유효기간만료' GROUP BY ph.pointEntity.pointIdx")
    List<PointHistoryEntity> checkExprieDate();

}
