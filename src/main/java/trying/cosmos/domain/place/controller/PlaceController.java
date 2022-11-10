package trying.cosmos.domain.place.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.domain.place.dto.response.PlaceFindResponse;
import trying.cosmos.domain.place.dto.response.PlaceListDistanceResponse;
import trying.cosmos.domain.place.service.PlaceService;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    public PlaceListDistanceResponse findByName(@RequestParam String name,
                                                @RequestParam Double latitude,
                                                @RequestParam Double longitude,
                                                @PageableDefault Pageable pageable) {
        return new PlaceListDistanceResponse(placeService.findByName(name, latitude, longitude, pageable));
    }

    @GetMapping("/position")
    public PlaceListDistanceResponse findByPosition(@RequestParam Double latitude,
                                                    @RequestParam Double longitude,
                                                    @RequestParam Double distance,
                                                    @PageableDefault Pageable pageable) {
        return new PlaceListDistanceResponse(placeService.findByPosition(latitude, longitude, distance, pageable));
    }

    @GetMapping("/{placeId}")
    public PlaceFindResponse find(@PathVariable Long placeId) {
        return new PlaceFindResponse(placeService.find(placeId));
    }
}
