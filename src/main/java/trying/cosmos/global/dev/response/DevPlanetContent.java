package trying.cosmos.global.dev.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.planet.entity.Planet;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DevPlanetContent {

    private Long planetId;
    private String name;
    private int dday;
    private String image;
    private String code;

    public DevPlanetContent(Planet planet) {
        this.planetId = planet.getId();
        this.name = planet.getName();
        this.dday = planet.getDday();
        this.image = planet.getImage();
        this.code = planet.getInviteCode();
    }
}
