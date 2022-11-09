package trying.cosmos.domain.course.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.common.DateAuditingEntity;
import trying.cosmos.domain.planet.entity.Planet;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = PROTECTED)
public class Course extends DateAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id")
    private Planet planet;

    private String title;

    @Column(length = 1000)
    private String body;

    private boolean isDeleted;

    @ToString.Exclude
    @OneToMany(mappedBy = "course")
    private List<Tag> tags = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "course")
    private List<CourseImage> images = new ArrayList<>();

    public void setPlanet(Planet planet) {
        this.planet = planet;
    }

    // Constructor
    public Course(Planet planet, String title, String body) {
        this.planet = planet;
        this.title = title;
        this.body = body;
        this.isDeleted = false;
    }

    public void update(String title, String body) {
        this.title = title;
        this.body = body;
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
