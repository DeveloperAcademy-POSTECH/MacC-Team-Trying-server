package trying.cosmos.domain.course.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.course.entity.Tag;
import trying.cosmos.domain.place.dto.response.PlaceFindContent;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagFindResponse {

    private String name;
    private PlaceFindContent place;

    public TagFindResponse(Tag tag) {
        this.name = tag.getName();
        this.place = new PlaceFindContent(tag.getPlace());
    }
}
