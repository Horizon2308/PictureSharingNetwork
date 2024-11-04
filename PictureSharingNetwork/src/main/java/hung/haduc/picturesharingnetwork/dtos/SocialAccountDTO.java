package hung.haduc.picturesharingnetwork.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SocialAccountDTO {

    @JsonProperty("google_account_id")
    protected String googleAccountId;

    @JsonProperty("facebook_account_id")
    protected String facebookAccountId;

    public boolean isGoogleAccountIdValid() {
        return this.googleAccountId != null && !this.googleAccountId.isEmpty();
    }

    public boolean isFacebookAccountIdValid() {
        return this.facebookAccountId != null && !this.facebookAccountId.isEmpty();
    }

    //Check user login social account
    public boolean isSocialLogin() {
        return (this.googleAccountId != null && !this.googleAccountId.isEmpty()) ||
                this.facebookAccountId != null && !this.facebookAccountId.isEmpty();
    }

}
