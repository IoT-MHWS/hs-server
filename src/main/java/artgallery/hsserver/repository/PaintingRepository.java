package artgallery.hsserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import artgallery.hsserver.model.PaintingEntity;

@Repository
public interface PaintingRepository extends JpaRepository<PaintingEntity, Long> {
}
