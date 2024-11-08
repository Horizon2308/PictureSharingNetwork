package hung.haduc.picturesharingnetwork.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {

    @NotEmpty(message = "Title must not be blank !")
    private String title;

    private String description;

    private String location;

    private String tag;

    @JsonProperty("share_for")
    private int shareFor;

    private Long likes;

    private Long comments;

}
