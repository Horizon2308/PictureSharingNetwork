package hung.haduc.picturesharingnetwork.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentListResponse {

    @JsonProperty("comments")
    private Page<CommentResponse> commentsList;

    @JsonProperty("total_page")
    private int totalPage;
}
