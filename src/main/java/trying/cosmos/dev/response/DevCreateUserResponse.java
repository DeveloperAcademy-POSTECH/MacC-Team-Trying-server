package trying.cosmos.dev.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DevCreateUserResponse {

    private String email;
    private String password;
    private String name;
    public DevCreateUserResponse(User user) {
        this.email = user.getEmail();
        this.password = "password";
        this.name = user.getName();
    }
}
