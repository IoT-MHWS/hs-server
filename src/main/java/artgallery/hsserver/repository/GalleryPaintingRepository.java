package artgallery.hsserver.repository;

import artgallery.hsserver.model.GalleryPaintingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GalleryPaintingRepository extends JpaRepository<GalleryPaintingEntity, Long> {
  Optional<GalleryPaintingEntity> findByPaintingId(Long id);
  Optional<GalleryPaintingEntity> findByGalleryId(Long id);
//  Optional<GalleryPaintingEntity> deleteGalleryPaintingEntityByPaintingId(Long id);
//  Optional<GalleryPaintingEntity> deleteGalleryPaintingEntityByGalleryId(Long id);
  void deleteGalleryPaintingEntityByGalleryId(Long id);
  void deleteGalleryPaintingEntityByPaintingId(Long id);
//  Optional<GalleryPaintingEntity> findAllByGalleryId(Long id);
//  void deleteAll(Optional<GalleryPaintingEntity> galleryPaintings);
}
