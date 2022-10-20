package trying.cosmos.domain.course;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coures_id")
    private Course course;

    private String name;

    public CourseImage(Course course, String name) {
        this.course = course;
        this.name = name;
        course.getImages().add(this);
    }
}
