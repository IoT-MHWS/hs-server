package artgallery.hsserver.repository;

import artgallery.hsserver.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
    Optional<UserEntity> findUserById(Long id);
    Optional<UserEntity> findByLogin(String username);
}
