package trying.cosmos.global.utils.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.global.auth.TokenProvider;
import trying.cosmos.global.utils.dev.response.DevCreateUserResponse;
import trying.cosmos.global.utils.dev.response.DevPlanetResponse;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevController {

    private final DevService devService;
    private final TokenProvider tokenProvider;

    @PostMapping("/users")
    public DevCreateUserResponse createUser() {
        User user = devService.createUser();
        return new DevCreateUserResponse(user, tokenProvider.getAccessToken(user));
    }

    @PostMapping("/planets")
    public DevPlanetResponse createPlanet() {
        User user = devService.createUser();
        return new DevPlanetResponse(devService.createPlanet(user), tokenProvider.getAccessToken(user));
    }
}
