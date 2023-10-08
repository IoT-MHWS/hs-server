package artgallery.hsserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import artgallery.hsserver.model.ArtistEntity;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
}


