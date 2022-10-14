package trying.cosmos.controller.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.component.PlanetImageType;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetListFindContent {

    private Long id;
    private String name;
    private List<String> owners = new ArrayList<>();
    private PlanetImageType imageType;

    public PlanetListFindContent(Planet planet) {
        this.id = planet.getId();
        this.name = planet.getName();
        this.owners = planet.getOwnersName();
        this.imageType = planet.getImageType();
    }
}
