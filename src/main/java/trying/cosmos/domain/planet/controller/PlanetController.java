package trying.cosmos.domain.planet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.domain.planet.dto.request.PlanetCreateRequest;
import trying.cosmos.domain.planet.dto.request.PlanetJoinRequest;
import trying.cosmos.domain.planet.dto.request.PlanetUpdateRequest;
import trying.cosmos.domain.planet.dto.response.PlanetCreateResponse;
import trying.cosmos.domain.planet.dto.response.PlanetFindResponse;
import trying.cosmos.domain.planet.dto.response.PlanetPreviewResponse;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.global.auth.AuthorityOf;
import trying.cosmos.global.auth.entity.AuthKey;
import trying.cosmos.global.auth.entity.Authority;
import trying.cosmos.global.utils.DateUtils;

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
    @GetMapping
    public PlanetPreviewResponse findPlanet(@RequestParam String code) {
        return new PlanetPreviewResponse(planetService.find(code));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/join")
    public void joinPlanet(@RequestBody @Validated PlanetJoinRequest request) {
        planetService.join(AuthKey.getKey(), request.getCode());
    }

    @AuthorityOf(Authority.USER)
    @PutMapping
    public PlanetFindResponse update(@RequestBody @Validated PlanetUpdateRequest request) {
        return new PlanetFindResponse(planetService.update(AuthKey.getKey(), request.getName(), DateUtils.stringToDate(request.getDate()), request.getImage()));
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping
    public void leave() {
        planetService.leave(AuthKey.getKey());
    }
}
