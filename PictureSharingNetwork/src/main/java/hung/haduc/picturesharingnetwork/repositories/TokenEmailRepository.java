package hung.haduc.picturesharingnetwork.repositories;

import hung.haduc.picturesharingnetwork.models.TokenEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenEmailRepository extends JpaRepository<TokenEmail, Long> {
    Optional<TokenEmail> findByToken(String activateCode);
}
