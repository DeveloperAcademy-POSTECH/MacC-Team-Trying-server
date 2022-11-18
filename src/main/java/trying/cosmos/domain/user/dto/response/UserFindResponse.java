package trying.cosmos.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import trying.cosmos.domain.user.entity.User;

@ToString
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserFindResponse {

    private UserFindContent me;
    private UserFindContent mate;
    private UserPlanetResponse planet;
    private boolean isSocialAccount;
    private boolean hasNotification;
    private boolean allowNotification;
    private UserActivityResponse activities;

    public UserFindResponse(User user, boolean hasNotification, UserActivityResponse activities) {
        this.me = new UserFindContent(user.getName(), user.getEmail());
        if (user.getPlanet() != null) {
            this.planet = new UserPlanetResponse(user.getPlanet(), user.getMate() != null);
        }
        if (user.getMate() != null) {
            this.mate = new UserFindContent(user.getMate().getName(), null);
        }
        this.isSocialAccount = user.isSocialAccount();
        this.hasNotification = hasNotification;
        this.allowNotification = user.isAllowNotification();
        this.activities = activities;
    }
}
