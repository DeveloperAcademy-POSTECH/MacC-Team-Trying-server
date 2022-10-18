package trying.cosmos.domain.planet;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.domain.planet.planet.*;
import trying.cosmos.domain.planet.response.PlanetCreateRequest;
import trying.cosmos.domain.planet.response.PlanetJoinRequest;
import trying.cosmos.global.auth.AuthKey;
import trying.cosmos.global.auth.Authority;
import trying.cosmos.global.auth.AuthorityOf;

@RestController
@RequestMapping("/planets")
@RequiredArgsConstructor
public class PlanetController {

    private final PlanetService planetService;

    @AuthorityOf(Authority.USER)
    @PostMapping
    public PlanetCreateResponse create(@RequestBody @Validated PlanetCreateRequest request) {
        Planet planet = planetService.create(AuthKey.get(), request.getName(), request.getImage());
        return new PlanetCreateResponse(planet);
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/{id}/code")
    public PlanetInviteCodeResponse getInviteCode(@PathVariable Long id) {
        return new PlanetInviteCodeResponse(planetService.getInviteCode(AuthKey.get(), id));
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/join")
    public PlanetFindResponse findPlanet(@RequestParam String code) {
        return new PlanetFindResponse(planetService.find(code));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/join")
    public void joinPlanet(@RequestBody @Validated PlanetJoinRequest request) {
        planetService.join(AuthKey.get(), request.getCode());
    }

    @GetMapping("/{id}")
    public PlanetFindResponse findPlanet(@PathVariable Long id) {
        return new PlanetFindResponse(planetService.find(id));
    }

    @GetMapping
    public PlanetListFindResponse findPlanets(@RequestParam(required = false) String query, Pageable pageable) {
        return new PlanetListFindResponse(planetService.searchPlanets(query, pageable));
    }

    @GetMapping("/{id}/courses")
    public PlanetCourseListResponse findPlanetCourses(@PathVariable Long id, Pageable pageable) {
        return new PlanetCourseListResponse(planetService.findPlanetCourses(AuthKey.isAuthenticated() ? AuthKey.get() : null, id, pageable));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/{id}/follow")
    public void follow(@PathVariable Long id) {
        planetService.follow(AuthKey.get(), id);
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{id}/follow")
    public void unfollow(@PathVariable Long id) {
        planetService.unfollow(AuthKey.get(), id);
    }
}
