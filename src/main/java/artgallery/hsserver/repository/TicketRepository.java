package artgallery.hsserver.repository;

import artgallery.hsserver.model.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {
    Optional<TicketEntity> findById(UUID uuid);
}
