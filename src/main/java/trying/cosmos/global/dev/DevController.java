package trying.cosmos.global.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.global.auth.SessionService;
import trying.cosmos.global.auth.TokenProvider;
import trying.cosmos.global.auth.entity.Session;
import trying.cosmos.global.dev.response.DevPlanetResponse;
import trying.cosmos.global.dev.response.DevUserResponse;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevController {

    private final DevService devService;
    private final TokenProvider tokenProvider;
    private final SessionService sessionService;
    private final CourseService courseService;

    @PostMapping("/user")
    public DevUserResponse createUser() {
        User user = devService.createUser();
        Session auth = sessionService.create(user);
        return new DevUserResponse(user, tokenProvider.getAccessToken(auth));
    }

    @PostMapping("/planet")
    public DevPlanetResponse createPlanet() {
        User user = devService.createUser();
        Session auth = sessionService.create(user);
        return new DevPlanetResponse(devService.createPlanet(user), tokenProvider.getAccessToken(auth));
    }

    @PostMapping("/mate")
    public DevPlanetResponse createMate() {
        User user = devService.createUser();
        User mate = devService.createUser();
        Session auth = sessionService.create(user);
        return new DevPlanetResponse(devService.createMate(user, mate), tokenProvider.getAccessToken(auth));
    }

    @PostMapping("/push")
    public void pushTodayDate() {
        courseService.pushTodayCourse();
    }
}
