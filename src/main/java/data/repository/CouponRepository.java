package data.repository;

import data.entity.CouponEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Integer> {
    Page<CouponEntity> findByCpNameContainingOrCpDescriptionContaining(String cp_name, String cp_description, Pageable pageable);
    Page<CouponEntity> findByBuyable(String buyable, Pageable pageable);

}
