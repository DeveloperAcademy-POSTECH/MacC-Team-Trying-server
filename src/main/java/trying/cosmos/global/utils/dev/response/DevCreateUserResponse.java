package trying.cosmos.global.utils.dev.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.user.entity.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DevCreateUserResponse {

    private String email;
    private String password;
    private String name;
    private String accessToken;

    public DevCreateUserResponse(User user, String accessToken) {
        this.email = user.getEmail();
        this.password = "password";
        this.name = user.getName();
        this.accessToken = accessToken;
    }
}
