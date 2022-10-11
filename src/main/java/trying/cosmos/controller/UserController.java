package trying.cosmos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.controller.response.UserLoginResponse;
import trying.cosmos.service.UserService;
import trying.cosmos.service.request.UserJoinRequest;
import trying.cosmos.service.request.UserLoginRequest;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public void join(@RequestBody UserJoinRequest request) {
        userService.join(request);
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest request) {
        return new UserLoginResponse(userService.login(request));
    }
}
