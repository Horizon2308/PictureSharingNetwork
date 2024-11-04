package hung.haduc.picturesharingnetwork.services;

import hung.haduc.picturesharingnetwork.commons.EmailTemplateName;
import hung.haduc.picturesharingnetwork.dtos.AuthenticationDTO;
import hung.haduc.picturesharingnetwork.exceptions.DataExistException;
import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.models.Role;
import hung.haduc.picturesharingnetwork.models.TokenEmail;
import hung.haduc.picturesharingnetwork.models.User;
import hung.haduc.picturesharingnetwork.repositories.RoleRepository;
import hung.haduc.picturesharingnetwork.repositories.TokenEmailRepository;
import hung.haduc.picturesharingnetwork.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${application.font-end.confimation-url}")
    private String confirmationUrl;

    @Transactional
    public void register (AuthenticationDTO authenticationDTO) throws MessagingException {
        if (userRepository.existsByEmail(authenticationDTO.getEmail())) {
            throw new DataExistException("Email already exist !");
        }
        Role role = roleRepository.findById(1L).orElseThrow(() -> new DataNotFoundException("Role not found !"));
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

    private void sendValidationEmail(User newUser) throws MessagingException {
        var newToken = generateAndSaveActivationToken(newUser);
        emailService.sendMail(
                newUser.getEmail(),
                newUser.getFullName(),
                EmailTemplateName.ACTIVATED_ACCOUNT,
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
}
