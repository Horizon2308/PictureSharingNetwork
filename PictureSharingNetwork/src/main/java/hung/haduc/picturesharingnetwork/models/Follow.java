package hung.haduc.picturesharingnetwork.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "follows", uniqueConstraints = {@UniqueConstraint(columnNames = {"follower_id", "followed_id"})})
@Builder
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User followerId;

    @ManyToOne
    @JoinColumn(name = "followed_id")
    private User followedId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime followedAt;

}
