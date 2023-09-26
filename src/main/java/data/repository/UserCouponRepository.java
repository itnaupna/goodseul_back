package data.repository;

import data.entity.UserCouponEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity, Integer> {
    boolean existsByCpNumber (String cpNumber);
}
