package artgallery.hsserver.repository;

import artgallery.hsserver.model.GalleryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GalleryRepository extends JpaRepository<GalleryEntity, UUID>{
    Optional<GalleryEntity> findByName(String name);
}
