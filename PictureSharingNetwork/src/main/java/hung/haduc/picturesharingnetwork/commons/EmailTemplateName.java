package hung.haduc.picturesharingnetwork.commons;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATED_ACCOUNT("active_account");

    private final String name;
    EmailTemplateName(String name) {
        this.name = name;
    }
}
