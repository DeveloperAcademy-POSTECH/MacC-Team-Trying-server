package trying.cosmos.domain.course.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.global.auditing.DateAuditingEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
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

    private LocalDate date;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CoursePlace> places = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseReview> reviews = new ArrayList<>();

    private boolean isDeleted;

    public void setPlanet(Planet planet) {
        this.planet = planet;
    }

    // Constructor
    public Course(Planet planet, String title, LocalDate date) {
        this.planet = planet;
        this.title = title;
        this.date = date;
        this.isDeleted = false;
    }

    public void update(String title, LocalDate date) {
        this.title = title;
        this.date = date;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public boolean isReviewed(User user) {
        return reviews.stream().anyMatch(r -> r.getWriter().equals(user));
    }

    public Optional<CourseReview> getReview(User user) {
        return reviews.stream().filter(r -> r.getWriter().equals(user)).findFirst();
    }
}
