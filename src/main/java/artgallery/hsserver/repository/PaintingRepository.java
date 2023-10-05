package artgallery.hsserver.repository;

import artgallery.hsserver.model.PaintingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {
//    Optional<PaintingEntity> findByName(String name);
}

