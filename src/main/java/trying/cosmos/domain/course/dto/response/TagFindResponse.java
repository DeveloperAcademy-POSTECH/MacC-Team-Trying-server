package trying.cosmos.domain.course.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.entity.Tag;
import trying.cosmos.domain.place.dto.response.PlaceFindResponse;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagFindResponse {

    private String name;
    private PlaceFindResponse place;

    public TagFindResponse(Tag tag) {
        this.name = tag.getName();
        this.place = new PlaceFindResponse(tag.getPlace());
    }
}
