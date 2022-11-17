package trying.cosmos.domain.place.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.domain.place.dto.response.PlaceFindResponse;
import trying.cosmos.domain.place.service.PlaceService;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/{placeId}")
    public PlaceFindResponse find(@PathVariable Long placeId) {
        return new PlaceFindResponse(placeService.find(placeId));
    }
}
