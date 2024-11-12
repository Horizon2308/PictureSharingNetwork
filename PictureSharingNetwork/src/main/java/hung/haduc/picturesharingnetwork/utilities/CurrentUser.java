package hung.haduc.picturesharingnetwork.utilities;

import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.models.User;
import hung.haduc.picturesharingnetwork.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUser {

    private final UserRepository userRepository;

    public User getCurrentUser() throws DataNotFoundException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findUserByEmail(authentication.getName())
                .orElseThrow(() -> new DataNotFoundException(("Current user not found !")));
    }

}
