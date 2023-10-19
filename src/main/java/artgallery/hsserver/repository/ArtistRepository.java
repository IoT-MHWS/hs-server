package artgallery.hsserver.repository;

import artgallery.hsserver.model.ArtistEntity;
import artgallery.hsserver.model.Style;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
  List<ArtistEntity> findByStyleAndYearOfBirthLessThanEqual(Style style, Integer yearOfBirth);
}


