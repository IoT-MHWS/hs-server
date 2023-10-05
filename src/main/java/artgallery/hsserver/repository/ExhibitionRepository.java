package artgallery.hsserver.repository;

import artgallery.hsserver.model.ExhibitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExhibitionRepository extends JpaRepository<ExhibitionEntity, UUID>{
    Optional<ExhibitionEntity> findByName(String name);
}