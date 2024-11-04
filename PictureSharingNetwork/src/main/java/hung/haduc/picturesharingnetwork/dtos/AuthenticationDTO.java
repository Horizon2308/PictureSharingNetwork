package hung.haduc.picturesharingnetwork.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationDTO extends SocialAccountDTO {

    @NotEmpty(message = "Full name can't be empty")
    @Size(min = 3, max = 30, message = "Full name must be between 3 to 30")
    @JsonProperty("full_name")
    private String fullName;


    @Email(message = "Email is invalid !")
    private String email;

    @NotEmpty(message = "Password can't be empty")
    @Size(min = 3, max = 30, message = "Password must be between 3 to 30")
    private String password;

    private String avatar;

    @JsonProperty("more_about_me")
    private String moreAboutMe;

    @NotEmpty(message = "Nickname can't be empty")
    @Size(min = 3, max = 30, message = "Nickname must be between 3 to 30")
    @JsonProperty("nick_name")
    private String nickName;

}
