package hung.haduc.picturesharingnetwork.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    @NotEmpty(message = "Comment can not be empty !")
    @Max(value = 500, message = "Maximum of comment text is 500 characters !")
    private String text;

    @JsonProperty("parent_id")
    private Long parentId;

}
