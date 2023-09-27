package data.repository;




import data.entity.GoodseulEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoodseulRepository extends JpaRepository<GoodseulEntity, Long> {
    Optional<GoodseulEntity> findById(Long id);
}
