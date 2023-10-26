package artgallery.hsserver.repository;

import artgallery.hsserver.model.GalleryPaintingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GalleryPaintingRepository extends JpaRepository<GalleryPaintingEntity, Long> {
  boolean existsByGalleryId(Long id);
  boolean existsByPaintingId(Long id);
  void deleteGalleryPaintingEntityByGalleryId(Long id);
  void deleteGalleryPaintingEntityByPaintingId(Long id);
  void deleteGalleryPaintingEntityByGalleryIdAndPaintingId(long id0, long id1);

  boolean existsByGalleryIdAndPaintingId(long galleryId, long paintingId);

  List<GalleryPaintingEntity> findByGalleryId(long galleryId);

  Optional<GalleryPaintingEntity> findByGalleryIdAndPaintingId(long galleryId, long paintingId);

  List<GalleryPaintingEntity> findByPaintingId(long paintingId);
}
