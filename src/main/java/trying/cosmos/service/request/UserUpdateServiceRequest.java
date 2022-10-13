package trying.cosmos.service.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.controller.request.UserUpdateControllerRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateServiceRequest {

    private Long userId;
    private String name;
    private String password;

    public UserUpdateServiceRequest(Long userId, UserUpdateControllerRequest request) {
        this.userId = userId;
        this.name = request.getName();
        this.password = request.getPassword();
    }
}
