package trying.cosmos.global.dev.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TestUserJoinRequest {

    private String email;
    private String password;
    private String name;
}
