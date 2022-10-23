package trying.cosmos.domain.course.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.common.DateAuditingEntity;
import trying.cosmos.domain.planet.entity.Planet;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Course extends DateAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id")
    private Planet planet;

    private String title;

    @Column(length = 1000)
    private String body;

    @Enumerated(EnumType.STRING)
    private trying.cosmos.domain.course.entity.Access access;

    private boolean isDeleted;

    @OneToMany(mappedBy = "course")
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<CourseImage> images = new ArrayList<>();

    // Constructor
    public Course(Planet planet, String title, String body, trying.cosmos.domain.course.entity.Access access) {
        this.planet = planet;
        this.title = title;
        this.body = body;
        this.access = access;
        this.isDeleted = false;
    }

    public void update(String title, String body, Access access) {
        this.title = title;
        this.body = body;
        this.access = access;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void clearImage() {
        this.images.clear();
    }

    public void clearTag() {
        this.tags.clear();
    }
}
