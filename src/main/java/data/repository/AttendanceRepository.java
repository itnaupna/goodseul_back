package data.repository;

import data.entity.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer> {

    Optional<AttendanceEntity> findAttendanceEntityByUserIdx(int userIdx);
}
