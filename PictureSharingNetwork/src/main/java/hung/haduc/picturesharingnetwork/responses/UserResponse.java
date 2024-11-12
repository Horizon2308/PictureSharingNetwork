package hung.haduc.picturesharingnetwork.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    @JsonProperty("full_name")
    private String fullName;

    private String email;

    private String avatar;

    @JsonProperty("more_about_me")
    private String moreAboutMe;

    @JsonProperty("nick_name")
    private String nickName;

}
