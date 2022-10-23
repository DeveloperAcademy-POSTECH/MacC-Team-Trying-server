package trying.cosmos.domain.course.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_image_id")
    private Long id;

    @ToString.Exclude
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
