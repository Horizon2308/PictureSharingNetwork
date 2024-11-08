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
@Table(name = "posts")
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    private String location;

    private String tag;

    private int shareFor;

    private String thumbnail;

    private Long likes;

    private Long comments;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;
}
