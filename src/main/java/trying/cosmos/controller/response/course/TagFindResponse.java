package trying.cosmos.controller.response.course;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.Place;
import trying.cosmos.entity.Tag;

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
