package trying.cosmos.global.utils.dev.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.planet.Planet;

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
