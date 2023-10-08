package artgallery.hsserver.repository;

import artgallery.hsserver.model.ExhibitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExhibitionRepository extends JpaRepository<ExhibitionEntity, Long> {
  Optional<ExhibitionEntity> findByName(String name);
}
