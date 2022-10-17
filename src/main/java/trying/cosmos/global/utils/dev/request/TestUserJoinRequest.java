package trying.cosmos.global.utils.dev.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TestUserJoinRequest {

    private String email;
    private String password;
    private String name;
}
