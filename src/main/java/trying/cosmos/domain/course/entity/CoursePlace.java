package trying.cosmos.domain.course.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.place.entity.Coordinate;
import trying.cosmos.domain.place.entity.Place;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoursePlace {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_place_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    private String memo;

    public CoursePlace(Course course, Place place, String memo) {
        this.course = course;
        this.place = place;
        this.memo = memo;
        course.getPlaces().add(this);
    }

    public Coordinate getCoordinate() {
        return new Coordinate(place.getLatitude(), place.getLongitude());
    }
}
