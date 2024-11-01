package hung.haduc.picturesharingnetwork.utilities;

import hung.haduc.picturesharingnetwork.exceptions.InvalidParameterException;
import hung.haduc.picturesharingnetwork.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class JwtTokenUtilities {
    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String generateToken(User user) throws InvalidParameterException {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("userId", user.getId());
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .setSubject(user.getEmail())
                    .signWith(this.getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new InvalidParameterException("Invalid parameters");
        }
    }

    private Key getSigningKey() {
        byte[] secretKeyDecoded = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(secretKeyDecoded);
    }

    private Claims exactAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T getClaim(String token, Function<Claims, T> claimResolve) {
        final Claims claims = this.exactAllClaims(token);
        return claimResolve.apply(claims);
    }

    public String exactEmail(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        final Date dateFromToken = this.getClaim(token, Claims::getExpiration);
        return dateFromToken.before(new Date());
    }

    public boolean validateToken(String token, User user) {
        final String emailFromToken = this.exactEmail(token);
        return (emailFromToken.equals(user.getEmail()) && !isTokenExpired(token));
    }
}
