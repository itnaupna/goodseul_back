package data.repository;




import data.entity.GoodseulEntity;
import data.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodseulRepository extends JpaRepository<GoodseulEntity, Long> {
    Optional<GoodseulEntity> findByIdx(Long idx);
    Page<GoodseulEntity> findAll(Pageable pageable);
}
