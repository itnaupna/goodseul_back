package data.repository;

import data.entity.OfferEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<OfferEntity, Integer> {

    Optional<OfferEntity> findByOfferIdx(int offerIdx);

    Optional<List<OfferEntity>> findAllByUserIdx(long userIdx);

    @Query(value = "SELECT * FROM offer WHERE write_date >= CURRENT_TIMESTAMP - INTERVAL 7 DAY", nativeQuery = true)
    Page<OfferEntity> findPostsWrittenInTheLastWeek(Pageable pageable);

}
