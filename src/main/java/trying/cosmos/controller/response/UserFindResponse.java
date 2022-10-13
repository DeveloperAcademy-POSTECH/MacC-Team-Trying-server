package trying.cosmos.controller.response;

import lombok.Getter;
import trying.cosmos.entity.User;

@Getter
public class UserFindResponse {

    private String email;
    private String name;

    public UserFindResponse(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
    }
}
