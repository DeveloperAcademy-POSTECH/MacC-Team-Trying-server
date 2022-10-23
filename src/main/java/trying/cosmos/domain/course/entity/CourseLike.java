package trying.cosmos.domain.course.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.user.entity.User;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public CourseLike(User user, Course course) {
        this.user = user;
        this.course = course;
    }
}
