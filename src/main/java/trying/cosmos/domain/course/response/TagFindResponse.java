package trying.cosmos.domain.course.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.Tag;
import trying.cosmos.domain.place.PlaceFindResponse;

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
