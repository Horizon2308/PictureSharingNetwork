package hung.haduc.picturesharingnetwork.models;

import hung.haduc.picturesharingnetwork.commons.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@Builder
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String text;

    @Column(nullable = false)
    private Long parent;

    private Long likes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

}
