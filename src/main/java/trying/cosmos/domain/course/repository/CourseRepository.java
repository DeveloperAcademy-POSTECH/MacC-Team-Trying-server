package trying.cosmos.domain.course.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.planet.entity.Planet;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {

    @Query("select c from Course c " +
            "where c.planet = :planet " +
            "and c.id = :courseId " +
            "and c.planet.isDeleted = false " +
            "and c.isDeleted = false")
    Optional<Course> searchById(Planet planet, Long courseId);

    @Query("select c from Course c " +
            "where c.planet = :planet " +
            "and c.date = :date " +
            "and c.planet.isDeleted = false " +
            "and c.isDeleted = false")
    Optional<Course> searchByDate(Planet planet, LocalDate date);

    @Query("select c from Course c " +
            "where c.planet = :planet " +
            "and c.title like :query " +
            "and c.planet.isDeleted = false " +
            "and c.isDeleted = false " +
            "order by c.createdDate desc")
    Slice<Course> searchCourses(Planet planet, String query, Pageable pageable);

    @Query("select cl.course from CourseLike cl " +
            "where cl.course.planet = :planet " +
            "and cl.course.title like :query " +
            "and cl.course.planet.isDeleted = false " +
            "and cl.course.isDeleted = false " +
            "order by cl.course.createdDate desc")
    Slice<Course> searchLikeCourses(Planet planet, String query, Pageable pageable);

    @Query("select c from Course c " +
            "where c.planet = :planet " +
            "and c.planet.isDeleted = false " +
            "and c.isDeleted = false ")
    Slice<Course> searchByPlanet(Planet planet, Pageable pageable);

    @Query("select count(c) from Course c where c.planet = :planet and c.isDeleted = false")
    int countByPlanet(Planet planet);

    @Query("select distinct c.date from Course c " +
            "where c.planet = :planet " +
            "and c.date >= :start and c.date < :end " +
            "and c.planet.isDeleted = false " +
            "and c.isDeleted = false " +
            "order by c.date")
    List<LocalDate> searchCourseDates(Planet planet, LocalDate start, LocalDate end);

    List<Course> findByDate(LocalDate today);
}
