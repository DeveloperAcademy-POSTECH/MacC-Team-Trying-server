package trying.cosmos.domain.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.planet.planet.PlanetFindContent;
import trying.cosmos.domain.user.User;

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
        this.planet = user.hasPlanet() ? new PlanetFindContent(user.getPlanet()) : null;
        this.mate = user.hasMate() ? new UserFindContent(user.getMate()) : null;
    }
}
