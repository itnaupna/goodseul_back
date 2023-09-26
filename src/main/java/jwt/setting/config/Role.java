package jwt.setting.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_LV0"),GOODSEUL("ROLE_LV1"),ADMIN("ROLE_LV2");
    private  final String key;
}
