package trying.cosmos.domain.user.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.user.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserFindContent {

    private String name;

    public UserFindContent(User user) {
        this.name = user.getName();
    }
}
