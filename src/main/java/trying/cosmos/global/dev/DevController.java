package trying.cosmos.global.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.global.auth.SessionService;
import trying.cosmos.global.auth.TokenProvider;
import trying.cosmos.global.auth.entity.Session;
import trying.cosmos.global.dev.response.DevCreateUserResponse;
import trying.cosmos.global.dev.response.DevPlanetResponse;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevController {

    private final DevService devService;
    private final TokenProvider tokenProvider;
    private final SessionService sessionService;

    @PostMapping("/users")
    public DevCreateUserResponse createUser() {
        User user = devService.createUser();
        Session auth = sessionService.create(user);
        return new DevCreateUserResponse(user, tokenProvider.getAccessToken(auth));
    }

    @PostMapping("/planets")
    public DevPlanetResponse createPlanet() {
        User user = devService.createUser();
        User mate = devService.createUser();
        Session auth = sessionService.create(user);
        return new DevPlanetResponse(devService.createPlanet(user, mate), tokenProvider.getAccessToken(auth));
    }
}
