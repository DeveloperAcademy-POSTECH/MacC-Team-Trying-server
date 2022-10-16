package trying.cosmos.controller.response.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserFindContent {

    private String name;

    public UserFindContent(User user) {
        this.name = user.getName();
    }
}
