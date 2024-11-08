package hung.haduc.picturesharingnetwork.services;

import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.models.Token;
import hung.haduc.picturesharingnetwork.models.User;
import hung.haduc.picturesharingnetwork.repositories.TokenRepository;
import hung.haduc.picturesharingnetwork.repositories.UserRepository;
import hung.haduc.picturesharingnetwork.utilities.JwtTokenUtilities;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final JwtTokenUtilities jwtTokenUtilities;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.expiration-refresh-token}")
    private Long expirationRefreshToken;

    private final int MAX_TOKENS = 3;

    @Transactional
    public Token addToken(User user, String token, boolean isMobileUser) throws DataNotFoundException {
        List<Token> tokens = tokenRepository.findAllByUser(user);
        if (tokens.size() >= MAX_TOKENS) {
            boolean hasNoneMobileToken = !tokens.stream().allMatch(Token::isMobile);
            Token deletedToken;
            if (hasNoneMobileToken) {
                deletedToken = tokens.stream().filter(tokenItem -> !tokenItem.isMobile())
                        .findFirst()
                        .orElseThrow(() -> new DataNotFoundException("Token not found !"));
            } else {
                deletedToken = tokens.get(0);
            }
            tokenRepository.delete(deletedToken);
        }
        Token generatedToken = Token.builder()
                .token(token)
                .tokenType("Bearer")
                .expired(false)
                .revoked(false)
                .isMobile(isMobileUser)
                .expirationDate(LocalDateTime.now().plusSeconds(expiration))
                .build();
        generatedToken.setRefreshToken(UUID.randomUUID().toString());
        generatedToken.setRefreshTokenExpirationDate(LocalDateTime.now()
                .plusSeconds(expirationRefreshToken));
        return generatedToken;
    }

}
