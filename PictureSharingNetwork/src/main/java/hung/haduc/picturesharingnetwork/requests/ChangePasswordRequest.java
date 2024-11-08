package hung.haduc.picturesharingnetwork.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordRequest {

    @Email(message = "Email is invalid !")
    private String email;

    @NotEmpty(message = "Current password must be empty !")
    @JsonProperty("current_password")
    private String currentPassword;

    @NotEmpty(message = "New password must be empty !")
    @JsonProperty("new_password")
    private String newPassword;

    @NotEmpty(message = "Retype new password must be empty !")
    @JsonProperty("retype_new_password")
    private String retypeNewPassword;

}
