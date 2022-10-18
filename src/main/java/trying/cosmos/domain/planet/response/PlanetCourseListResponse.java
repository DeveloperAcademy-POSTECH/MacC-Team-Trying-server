package trying.cosmos.domain.planet.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;
import trying.cosmos.domain.course.Course;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetCourseListResponse {

    private List<PlanetCourseContent> courses;
    private int size;
    private boolean hasNext;

    public PlanetCourseListResponse(Slice<Course> courseSlice) {
        this.courses = courseSlice.getContent().stream().map(PlanetCourseContent::new).collect(Collectors.toList());
        this.size = courseSlice.getNumberOfElements();
        this.hasNext = courseSlice.hasNext();
    }
}
