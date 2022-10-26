package trying.cosmos.domain.planet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.domain.planet.dto.request.PlanetUpdateRequest;
import trying.cosmos.domain.planet.dto.response.*;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.global.auth.AuthorityOf;
import trying.cosmos.global.auth.entity.AuthKey;
import trying.cosmos.global.auth.entity.Authority;

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
    public PlanetPreviewResponse findPlanet(@RequestParam String code) {
        return new PlanetPreviewResponse(planetService.find(code));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/join")
    public void joinPlanet(@RequestBody @Validated PlanetJoinRequest request) {
        planetService.join(AuthKey.getKey(), request.getCode());
    }

    @GetMapping("/{planetId}")
    public PlanetFindResponse findPlanet(@PathVariable Long planetId) {
        return planetService.find(AuthKey.getKey(), planetId);
    }

    @GetMapping
    public PlanetListFindResponse findPlanets(@RequestParam(required = false, defaultValue = "") String query, Pageable pageable) {
        return planetService.findList(AuthKey.getKey(), query, pageable);
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/follow")
    public PlanetListFindResponse findFollowPlanets(Pageable pageable) {
        return planetService.findFollowPlanets(AuthKey.getKey(), pageable);
    }

    @GetMapping("/{planetId}/courses")
    public PlanetCourseListResponse findPlanetCourses(@PathVariable Long planetId, Pageable pageable) {
        return new PlanetCourseListResponse(planetService.findPlanetCourse(AuthKey.getKey(), planetId, pageable));
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

    @AuthorityOf(Authority.USER)
    @PutMapping("/{planetId}")
    public void update(@PathVariable Long planetId, @RequestBody @Validated PlanetUpdateRequest request) {
        planetService.update(AuthKey.getKey(), planetId, request.getName(), request.getDday());
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{planetId}")
    public void leave(@PathVariable Long planetId) {
        planetService.leave(AuthKey.getKey(), planetId);
    }
}
