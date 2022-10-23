package trying.cosmos.global.dev.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.planet.entity.Planet;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DevPlanetResponse {

    private DevCreateUserResponse owner;
    private DevPlanetContent planet;

    public DevPlanetResponse(Planet planet, String accessToken) {
        this.owner = new DevCreateUserResponse(planet.getOwners().get(0), accessToken);
        this.planet = new DevPlanetContent(planet);
    }
}
