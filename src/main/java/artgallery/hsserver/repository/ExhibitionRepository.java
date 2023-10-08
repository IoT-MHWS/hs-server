package artgallery.hsserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import artgallery.hsserver.model.ExhibitionEntity;

@Repository
public interface ExhibitionRepository extends JpaRepository<ExhibitionEntity, Long>{
    Optional<ExhibitionEntity> findByName(String name);
}
