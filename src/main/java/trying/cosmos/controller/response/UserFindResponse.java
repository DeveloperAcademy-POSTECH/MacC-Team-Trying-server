package trying.cosmos.controller.response;

import lombok.Getter;
import trying.cosmos.entity.User;

@Getter
public class UserFindResponse {

    private String name;

    public UserFindResponse(User user) {
        this.name = user.getName();
    }
}
