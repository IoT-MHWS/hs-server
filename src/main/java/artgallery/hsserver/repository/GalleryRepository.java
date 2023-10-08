package artgallery.hsserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import artgallery.hsserver.model.GalleryEntity;

@Repository
public interface GalleryRepository extends JpaRepository<GalleryEntity, Long>{
}
