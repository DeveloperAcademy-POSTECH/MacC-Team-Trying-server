package trying.cosmos.domain.user.dto.response;

import lombok.*;
import trying.cosmos.domain.user.entity.User;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserFindContent {

    private String name;

    public UserFindContent(User user) {
        this.name = user.getName();
    }
}
