package artgallery.hsserver.repository;

import artgallery.hsserver.model.PaintingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaintingRepository extends JpaRepository<PaintingEntity, Long> {
}
