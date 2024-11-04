package hung.haduc.picturesharingnetwork.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private String refreshToken;

    @Column(length = 50)
    private String tokenType;

    private LocalDateTime expirationDate;

    private LocalDateTime refreshTokenExpirationDate;

    @Column(name = "is_mobile", columnDefinition = "TINYINT(1)")
    private boolean isMobile;

    private boolean revoked;

    private boolean expired;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
