package artgallery.hsserver.repository;

import artgallery.hsserver.model.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
  Optional<TicketEntity> findById(Long uuid);
}
