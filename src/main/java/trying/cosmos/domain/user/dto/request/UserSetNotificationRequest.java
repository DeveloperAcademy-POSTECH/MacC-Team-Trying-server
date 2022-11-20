package trying.cosmos.domain.user.dto.request;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserSetNotificationRequest {

    private boolean allow;
}
