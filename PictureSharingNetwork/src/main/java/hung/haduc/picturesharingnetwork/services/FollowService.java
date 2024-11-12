package hung.haduc.picturesharingnetwork.services;

import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.models.Follow;
import hung.haduc.picturesharingnetwork.models.User;
import hung.haduc.picturesharingnetwork.repositories.FollowRepository;
import hung.haduc.picturesharingnetwork.repositories.UserRepository;
import hung.haduc.picturesharingnetwork.utilities.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public void followByUserId(Long userId) throws DataNotFoundException {
        User followedUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found !"));
        User followerUser = currentUser.getCurrentUser();
        if (this.checkUserIsActivated(followedUser)) {
            followRepository.save(Follow.builder()
                    .followerId(followerUser)
                    .followedId(followedUser)
                    .build());
        }
    }

    public void unFollowByUserId(Long userId) throws DataNotFoundException {
        User followerUser = currentUser.getCurrentUser();
        User followedUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Followed user not found !"));
        followRepository.deleteFollowByFollowerIdAndFollowedId(followerUser, followedUser);
    }

    private boolean checkUserIsActivated(User user) {
        return user.isEnable();
    }

}
