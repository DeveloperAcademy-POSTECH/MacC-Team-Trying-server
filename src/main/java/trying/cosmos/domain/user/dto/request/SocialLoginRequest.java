package trying.cosmos.domain.user.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SocialLoginRequest {

    private String identifier;

    private String deviceToken;
}
