package trying.cosmos.global.utils.push;

import trying.cosmos.domain.user.entity.User;
import trying.cosmos.global.utils.push.dto.PushRequest;

public interface PushUtils {

    void pushAll(String title, String body);
    void pushTo(User user, String title, String body, PushRequest.Data data);
}
