package hung.haduc.picturesharingnetwork.repositories;

import hung.haduc.picturesharingnetwork.models.Token;
import hung.haduc.picturesharingnetwork.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllByUser(User user);
}
