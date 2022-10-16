package trying.cosmos.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.dev.response.DevCreateUserResponse;
import trying.cosmos.dev.response.DevPlanetResponse;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevController {

    private final DevService devService;

    @PostMapping("/users")
    public DevCreateUserResponse createUser() {
        return new DevCreateUserResponse(devService.createUser());
    }

    @PostMapping("/planets")
    public DevPlanetResponse createPlanet() {
        return new DevPlanetResponse(devService.createPlanet());
    }
}
