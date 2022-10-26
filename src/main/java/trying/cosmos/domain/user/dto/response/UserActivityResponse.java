package trying.cosmos.domain.user.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserActivityResponse {

    private int courseCount;
    private int followCount;
    private int likedCount;
}
