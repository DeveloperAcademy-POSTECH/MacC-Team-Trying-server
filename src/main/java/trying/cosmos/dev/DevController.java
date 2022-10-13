package trying.cosmos.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.controller.response.UserLoginResponse;
import trying.cosmos.dev.request.TestUserJoinRequest;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevController {

    private final DevService devService;

    @PostMapping("/users")
    public UserLoginResponse createUser(@RequestBody TestUserJoinRequest request) {
        return new UserLoginResponse(devService.createUser(request));
    }
}
