package artgallery.hsserver.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import artgallery.hsserver.model.PaintingEntity;

@Repository
public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {
//    Optional<PaintingEntity> findByName(String name);
}

