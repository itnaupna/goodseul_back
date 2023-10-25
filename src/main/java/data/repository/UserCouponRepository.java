package data.repository;

import data.entity.UserCouponEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity, Integer> {
    boolean existsByCpNumber (String cpNumber);

    @Query("SELECT uc FROM user_coupon uc LEFT JOIN coupon c on uc.coupon.cpIdx = c.cpIdx WHERE uc.userEntity.idx = :idx AND uc.coupon.cpStatus = 'Y'" +
            "AND uc.ucpStatus = 'N'")
    Page<UserCouponEntity> findByMemberIdx(@Param("idx") long idx, Pageable pageable);

    int countAllByUserEntity_Idx(long idx);


}
