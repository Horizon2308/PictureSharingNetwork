package hung.haduc.picturesharingnetwork.repositories;

import hung.haduc.picturesharingnetwork.models.TokenEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenEmailRepository extends JpaRepository<TokenEmail, Long> {
}
