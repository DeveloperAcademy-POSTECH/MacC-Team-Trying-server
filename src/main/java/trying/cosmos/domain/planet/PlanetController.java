package trying.cosmos.domain.planet;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.domain.planet.response.*;
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
        Planet planet = planetService.create(AuthKey.getKey(), request.getName(), request.getImage());
        return new PlanetCreateResponse(planet);
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/{planetId}/code")
    public PlanetInviteCodeResponse getInviteCode(@PathVariable Long planetId) {
        return new PlanetInviteCodeResponse(planetService.getInviteCode(AuthKey.getKey(), planetId));
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/join")
    public PlanetFindResponse findPlanet(@RequestParam String code) {
        return new PlanetFindResponse(planetService.find(code));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/join")
    public void joinPlanet(@RequestBody @Validated PlanetJoinRequest request) {
        planetService.join(AuthKey.getKey(), request.getCode());
    }

    @GetMapping("/{planetId}")
    public PlanetFindResponse findPlanet(@PathVariable Long planetId) {
        return new PlanetFindResponse(planetService.find(planetId));
    }

    @GetMapping
    public PlanetListFindResponse findPlanets(@RequestParam(required = false) String query, Pageable pageable) {
        return new PlanetListFindResponse(planetService.searchPlanets(query, pageable));
    }

    @GetMapping("/{planetId}/courses")
    public PlanetCourseListResponse findPlanetCourses(@PathVariable Long planetId, Pageable pageable) {
        return new PlanetCourseListResponse(planetService.findPlanetCourses(AuthKey.getKey(), planetId, pageable));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/{planetId}/follow")
    public void follow(@PathVariable Long planetId) {
        planetService.follow(AuthKey.getKey(), planetId);
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{planetId}/follow")
    public void unfollow(@PathVariable Long planetId) {
        planetService.unfollow(AuthKey.getKey(), planetId);
    }
}
