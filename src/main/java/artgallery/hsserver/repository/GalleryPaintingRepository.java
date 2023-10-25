package artgallery.hsserver.repository;

import artgallery.hsserver.model.GalleryPaintingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryPaintingRepository extends JpaRepository<GalleryPaintingEntity, Long> {
  boolean existsByGalleryId(Long id);
  boolean existsByPaintingId(Long id);
  void deleteGalleryPaintingEntityByGalleryId(Long id);
  void deleteGalleryPaintingEntityByPaintingId(Long id);
}
