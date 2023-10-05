package artgallery.hsserver.repository;

import artgallery.hsserver.model.ArtistEntity;
import artgallery.hsserver.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistRepository, UUID> {
    List<ArtistEntity> findByName(String name);
}


