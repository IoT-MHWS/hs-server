package artgallery.hsserver.repository;

import artgallery.hsserver.model.GalleryPaintingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GalleryPaintingRepository extends JpaRepository<GalleryPaintingEntity, Long> {
  void deleteGalleryPaintingEntityByGalleryId(Long id);
  void deleteGalleryPaintingEntityByPaintingId(Long id);
}
