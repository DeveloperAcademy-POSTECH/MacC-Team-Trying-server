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

    public UserFindResponse(User user) {
        this.me = new UserFindContent(user);
        if (user.getPlanet() != null) {
            this.planet = new UserPlanetResponse(user.getPlanet(), user.getMate() != null);
        }
        if (user.getMate() != null) {
            this.mate = new UserFindContent(user.getMate());
        }
    }
}
