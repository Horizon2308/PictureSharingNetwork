package hung.haduc.picturesharingnetwork.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import hung.haduc.picturesharingnetwork.models.PostImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailResponse {

    private String title;

    private String thumbnail;

    private String description;

    private String location;

    private String tag;

    private Long likes;

    private Long comments;

    @JsonProperty("post_images")
    private List<PostImage> postImages;

}
