package trying.cosmos.domain.course;

import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.common.DateAuditingEntity;
import trying.cosmos.domain.planet.Planet;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

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
    private Access access;

    @OneToMany(mappedBy = "course", cascade = ALL)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = ALL)
    private List<CourseImage> images = new ArrayList<>();

    // Constructor
    public Course(Planet planet, String title, String body, Access access) {
        this.planet = planet;
        this.title = title;
        this.body = body;
        this.access = access;
    }
}
