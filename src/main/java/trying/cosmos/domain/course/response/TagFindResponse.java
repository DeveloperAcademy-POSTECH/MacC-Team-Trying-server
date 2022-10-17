package trying.cosmos.domain.course.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.Tag;
import trying.cosmos.domain.place.Place;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagFindResponse {

    private String name;
    private Place place;

    public TagFindResponse(Tag tag) {
        this.name = tag.getName();
        this.place = tag.getPlace();
    }
}
