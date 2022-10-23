package trying.cosmos.domain.planet.dto.response;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanetInviteCodeResponse {

    private String code;
}
