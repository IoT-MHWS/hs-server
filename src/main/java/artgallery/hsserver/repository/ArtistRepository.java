package artgallery.hsserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import artgallery.hsserver.model.ArtistEntity;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistRepository, UUID> {
    List<ArtistEntity> findByName(String name);
}


