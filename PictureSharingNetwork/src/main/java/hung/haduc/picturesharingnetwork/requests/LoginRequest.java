package hung.haduc.picturesharingnetwork.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @Email(message = "Email is invalid !")
    private String email;

    private String password;

    // Facebook Account Id, not mandatory, can be blank
    @JsonProperty("facebook_account_id")
    private String facebookAccountId;

    // Google Account Id, not mandatory, can be blank
    @JsonProperty("google_account_id")
    private String googleAccountId;

    // Kiểm tra facebookAccountId có hợp lệ không
    public boolean isFacebookAccountIdValid() {
        return facebookAccountId != null && !facebookAccountId.isEmpty();
    }

    // Kiểm tra googleAccountId có hợp lệ không
    public boolean isGoogleAccountIdValid() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }
}
