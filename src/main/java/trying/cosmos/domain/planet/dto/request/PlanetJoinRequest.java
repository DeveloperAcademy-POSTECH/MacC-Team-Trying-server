package trying.cosmos.domain.planet.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanetJoinRequest {

    private String code;
}
