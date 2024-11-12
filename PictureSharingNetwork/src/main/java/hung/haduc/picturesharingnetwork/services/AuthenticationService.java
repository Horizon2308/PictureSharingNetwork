package hung.haduc.picturesharingnetwork.services;

import hung.haduc.picturesharingnetwork.commons.EmailTemplateName;
import hung.haduc.picturesharingnetwork.dtos.AuthenticationDTO;
import hung.haduc.picturesharingnetwork.dtos.UserUpdateDTO;
import hung.haduc.picturesharingnetwork.exceptions.DataExistException;
import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.exceptions.InvalidParameterException;
import hung.haduc.picturesharingnetwork.models.Role;
import hung.haduc.picturesharingnetwork.models.TokenEmail;
import hung.haduc.picturesharingnetwork.models.User;
import hung.haduc.picturesharingnetwork.repositories.RoleRepository;
import hung.haduc.picturesharingnetwork.repositories.TokenEmailRepository;
import hung.haduc.picturesharingnetwork.repositories.UserRepository;
import hung.haduc.picturesharingnetwork.requests.ChangePasswordRequest;
import hung.haduc.picturesharingnetwork.requests.ForgotPasswordRequest;
import hung.haduc.picturesharingnetwork.requests.LoginRequest;
import hung.haduc.picturesharingnetwork.responses.UserResponse;
import hung.haduc.picturesharingnetwork.utilities.CurrentUser;
import hung.haduc.picturesharingnetwork.utilities.JwtTokenUtilities;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenEmailRepository tokenEmailRepository;
    private final CurrentUser currentUser;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtilities jwtTokenUtilities;


    @Value("${application.font-end.confirmation-url}")
    private String confirmationUrl;

    @Transactional
    public void register (AuthenticationDTO authenticationDTO) throws MessagingException, DataExistException, DataNotFoundException {
        if (userRepository.existsByEmail(authenticationDTO.getEmail())) {
            throw new DataExistException("Email already exist !");
        }
        Role role = roleRepository.findById(1L)
                .orElseThrow(() -> new DataNotFoundException("Role not found !"));
        User newUser = User.builder()
                .email(authenticationDTO.getEmail())
                .avatar(authenticationDTO.getAvatar())
                .enable(false)
                .accountLooked(false)
                .facebookAccountId(authenticationDTO.getFacebookAccountId())
                .googleAccountId(authenticationDTO.getGoogleAccountId())
                .fullName(authenticationDTO.getFullName())
                .moreAboutMe(authenticationDTO.getMoreAboutMe())
                .nickname(authenticationDTO.getNickName())
                .role(role)
                .build();
        if (!authenticationDTO.isSocialLogin()) {
            String encodedPassword = passwordEncoder.encode(authenticationDTO.getPassword());
            newUser.setPassword(encodedPassword);
        }
        userRepository.save(newUser);
        sendValidationEmail(newUser);
    }

    public String login(LoginRequest loginRequest) throws DataNotFoundException, InvalidParameterException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword(),
                        null
                );
        var authentication = authenticationManager.authenticate(authenticationToken);
        if (authentication.isAuthenticated()) {
            User user = userRepository.findUserByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new DataNotFoundException("User not found !"));
            if (!user.isEnable()) {
                throw new InvalidParameterException("Your account must be confirmed by activated code " +
                        "that we sent to your email before login !");
            }
            return jwtTokenUtilities.generateToken(user);
        } else {
            throw new InvalidParameterException("Email or password is incorrect !");
        }
    }

    @Transactional
    public void activateAccount(String activateCode) throws InvalidParameterException,
            DataNotFoundException,
            MessagingException {
        TokenEmail tokenEmail = tokenEmailRepository.findByToken(activateCode)
                .orElseThrow(() -> new InvalidParameterException("Token is incorrect !"));
        if (LocalDateTime.now().isAfter(tokenEmail.getExpiredAt())) {
            sendValidationEmail(userRepository.findById(tokenEmail.getUserId().getId()).
                    orElseThrow(() -> new DataNotFoundException("User not found !")));
            throw new RuntimeException("Activation token has expired. " +
                    "A new token has been send to the same email address");
        }
        var user = userRepository.findById(tokenEmail.getUserId().getId())
                .orElseThrow(() -> new DataNotFoundException("User not found !"));
        user.setEnable(true);
        tokenEmail.setValidatedAt(LocalDateTime.now());
        userRepository.save(user);
        tokenEmailRepository.save(tokenEmail);
    }

    public User getUserDetailFromToken(String token) throws DataNotFoundException {
        String email = jwtTokenUtilities.exactEmail(token);
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found !"));
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest)
            throws InvalidParameterException, MessagingException {
        User user = userRepository.findUserByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new InvalidParameterException("Email is incorrect !"));
        String newPassword = this.generateNewPassword();
        emailService.sendNewPassword(
                forgotPasswordRequest.getEmail(),
                user.getFullName(),
                "forgot_password",
                newPassword,
                ""
        );
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest)
            throws InvalidParameterException, DataNotFoundException {
        if (!changePasswordRequest.getNewPassword().
                equals(changePasswordRequest.getRetypeNewPassword())) {
            throw new InvalidParameterException("New password and retype new password don't match !");
        }
        User user = userRepository.findUserByEmail(changePasswordRequest.getEmail())
                .orElseThrow(() -> new DataNotFoundException("User not found !"));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                changePasswordRequest.getEmail(),
                changePasswordRequest.getCurrentPassword(),
                null
        );
        var authentication = authenticationManager.authenticate(authenticationToken);
        if (authentication.isAuthenticated()) {
            String encodeNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
            user.setPassword(encodeNewPassword);
            userRepository.save(user);
        }
    }

    @Transactional
    public UserResponse updateUser(UserUpdateDTO userUpdateDTO)
            throws DataNotFoundException {
        User updatedUser = currentUser.getCurrentUser();
        updatedUser.setAvatar(userUpdateDTO.getAvatar());
        updatedUser.setMoreAboutMe(userUpdateDTO.getMoreAboutMe());
        updatedUser.setNickname(userUpdateDTO.getNickName());
        User user = userRepository.save(updatedUser);
        return UserResponse.builder()
                .id(user.getId())
                .nickName(user.getNickname())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .moreAboutMe(user.getMoreAboutMe())
                .build();
    }

    @Transactional
    public void lockingAccount() throws DataNotFoundException {
        User user = currentUser.getCurrentUser();
        user.setAccountLooked(true);
        userRepository.save(user);
    }

    private void sendValidationEmail(User newUser) throws MessagingException {
        var newToken = generateAndSaveActivationToken(newUser);
        emailService.sendMail(
                newUser.getEmail(),
                newUser.getFullName(),
                "activate_account",
                confirmationUrl,
                newToken,
                "Activation Account"
        );
    }


    public String generateAndSaveActivationToken(User newUser) {
        String generatedTokenEmail = generateTokenEmail(6);
        TokenEmail tokenEmail = TokenEmail.builder()
                .token(generatedTokenEmail)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .userId(newUser)
                .build();
        tokenEmailRepository.save(tokenEmail);
        return generatedTokenEmail;
    }

    private String generateTokenEmail(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    private String generateNewPassword() {
        String characters = "0123456789qwertyuiopasdfghjklzxcvbnm";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

}
