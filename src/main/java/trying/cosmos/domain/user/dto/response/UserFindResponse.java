package trying.cosmos.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.planet.dto.response.PlanetFindContent;
import trying.cosmos.domain.user.entity.User;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserFindResponse {

    private UserFindContent me;
    private UserFindContent mate;
    private PlanetFindContent planet;

    public UserFindResponse(User user) {
        this.me = new UserFindContent(user);
        if (user.getPlanet() != null) {
            this.planet = new PlanetFindContent(user.getPlanet());
        }
        if (user.getMate() != null) {
            this.mate = new UserFindContent(user.getMate());
        }
    }
}
