package trying.cosmos.domain.planet.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.planet.entity.Planet;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetPreviewResponse {

    private String name;
    private String image;

    public PlanetPreviewResponse(Planet planet) {
        this.name = planet.getName();
        this.image = planet.getImage();
    }
}
