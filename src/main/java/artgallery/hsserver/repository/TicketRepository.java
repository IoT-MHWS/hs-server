package artgallery.hsserver.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import artgallery.hsserver.model.TicketEntity;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {
    Optional<TicketEntity> findById(UUID uuid);
}
