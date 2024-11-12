package hung.haduc.picturesharingnetwork.repositories;

import hung.haduc.picturesharingnetwork.models.Follow;
import hung.haduc.picturesharingnetwork.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> deleteFollowByFollowerIdAndFollowedId(User followerUser, User followedUser);
}
