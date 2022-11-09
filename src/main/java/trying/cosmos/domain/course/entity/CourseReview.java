package trying.cosmos.domain.course.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.global.auditing.DateAuditingEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseReview extends DateAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_review_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(length = 1000)
    private String body;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<CourseReviewImage> images = new ArrayList<>();

    public CourseReview(User writer, Course course, String body) {
        this.writer = writer;
        this.course = course;
        this.body = body;
        course.getReviews().add(this);
    }

    public void update(String body) {
        this.body = body;
    }
}
