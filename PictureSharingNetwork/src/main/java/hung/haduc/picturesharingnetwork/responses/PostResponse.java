package hung.haduc.picturesharingnetwork.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private String tag;

    private Long likes;

    private Long comments;

    private String nickname;

}
