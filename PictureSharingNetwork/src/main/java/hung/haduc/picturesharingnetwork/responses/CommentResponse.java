package hung.haduc.picturesharingnetwork.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import hung.haduc.picturesharingnetwork.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;

    private String text;

    private User user;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
