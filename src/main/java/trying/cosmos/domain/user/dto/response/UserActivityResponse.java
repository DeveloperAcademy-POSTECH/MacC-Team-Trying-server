package trying.cosmos.domain.user.dto.response;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserActivityResponse {

    private int courseCount;
    private int likedCount;
}
