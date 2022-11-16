package trying.cosmos.domain.course.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.dto.request.CoursePlaceRequest;
import trying.cosmos.domain.course.dto.response.CourseDateResponse;
import trying.cosmos.domain.course.dto.response.CourseFindResponse;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.CourseLike;
import trying.cosmos.domain.course.entity.CoursePlace;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.coursereview.entity.CourseReview;
import trying.cosmos.domain.coursereview.repository.CourseReviewLikeRepository;
import trying.cosmos.domain.notification.entity.NotificationTarget;
import trying.cosmos.domain.notification.service.NotificationService;
import trying.cosmos.domain.place.repository.PlaceRepository;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.DateUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PlaceRepository placeRepository;
    private final CourseReviewLikeRepository courseReviewLikeRepository;
    private final NotificationService notificationService;
    private final MessageSourceAccessor messageSource;
    private final EntityManager em;

    @Transactional
    public Course create(Long userId, String title, LocalDate date, List<CoursePlaceRequest> placeRequests) {
        User user = userRepository.findById(userId).orElseThrow();

        if (user.getPlanet() == null) {
            throw new CustomException(ExceptionType.NO_PLANET);
        }
        if (courseRepository.searchByDate(user.getPlanet(), date).isPresent()) {
            throw new CustomException(ExceptionType.DUPLICATED);
        }
        Course course = courseRepository.save(new Course(user.getPlanet(), title, date));

        placeRequests.forEach(p ->
                new CoursePlace(course, placeRepository.findById(p.getPlaceId()).orElseThrow(), p.getMemo())
        );

        String[] args = new String[]{user.getName(), DateUtils.getFormattedDate(course.getDate(), "MM월 dd일"), course.getTitle()};
        notificationService.create(
                user.getMate(),
                messageSource.getMessage("notification.course.create.title"),
                messageSource.getMessage("notification.course.create.body", args),
                NotificationTarget.COURSE,
                course.getId()
        );

        return course;
    }

    public CourseFindResponse find(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();
        return new CourseFindResponse(course, isLiked(user, course));
    }

    public CourseFindResponse findByDate(Long userId, LocalDate date) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchByDate(user.getPlanet(), date).orElseThrow();
        return new CourseFindResponse(course, isLiked(user, course));
    }

    public CourseDateResponse findCourseDates(Long userId, LocalDate start, LocalDate end) {
        User user = userRepository.findById(userId).orElseThrow();
        return new CourseDateResponse(courseRepository.searchCourseDates(user.getPlanet(), start, end));
    }

    public Slice<CourseFindResponse> findList(Long userId, String title, boolean likeonly, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow();
        Planet planet = user.getPlanet();
        Slice<Course> courseSlice;
        if (likeonly) {
            courseSlice = courseRepository.searchLikeCourses(planet, "%" + title + "%", pageable);
        } else {
            courseSlice = courseRepository.searchCourses(planet, "%" + title + "%", pageable);
        }

        List<CourseFindResponse> contents = courseSlice.getContent().stream()
                .map(course -> new CourseFindResponse(course, isLiked(user, course)))
                .collect(Collectors.toList());
        return new SliceImpl<>(contents, courseSlice.getPageable(), courseSlice.hasNext());
    }

    public Slice<CourseFindResponse> findLogs(Long userId, Pageable pageable) {
        Slice<Course> courseSlice = courseRepository.getLogs(userRepository.findById(userId).orElseThrow(), pageable);
        User user = userRepository.findById(userId).orElseThrow();
        List<CourseFindResponse> contents = courseSlice.getContent().stream()
                .map(course -> new CourseFindResponse(course, isLiked(user, course)))
                .collect(Collectors.toList());
        return new SliceImpl<>(contents, courseSlice.getPageable(), courseSlice.hasNext());
    }

    private boolean isLiked(User user, Course course) {
        return courseReviewLikeRepository.existsByUserAndCourse(user, course);
    }

    @Transactional
    public void like(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();
        if (courseReviewLikeRepository.existsByUserAndCourse(user, course)) {
            throw new CustomException(ExceptionType.DUPLICATED);
        }
        courseReviewLikeRepository.save(new CourseLike(userRepository.findById(userId).orElseThrow(), courseRepository.findById(courseId).orElseThrow()));
    }

    @Transactional
    public void unlike(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();
        if (!courseReviewLikeRepository.existsByUserAndCourse(user, course)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        courseReviewLikeRepository.delete(courseReviewLikeRepository.findByUserAndCourse(user, course).orElseThrow());
    }

    public CourseReview findMyReview(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();

        return course.getReview(user).orElseThrow();
    }

    public CourseReview findMateReview(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();

        return course.getReview(user.getMate()).orElseThrow();
    }

    @Transactional
    public Course update(Long userId, Long courseId, String title, LocalDate date, List<CoursePlaceRequest> placeRequests) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();

        course.update(title, date);
        removeExistPlaces(course);
        placeRequests.forEach(p ->
                new CoursePlace(course, placeRepository.findById(p.getPlaceId()).orElseThrow(), p.getMemo())
        );
        em.flush();
        em.clear();

        return course;
    }

    private void removeExistPlaces(Course course) {
        course.getPlaces().forEach(em::remove);
        course.getPlaces().clear();
    }

    @Transactional
    public void delete(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();
        course.delete();
    }

    @Async
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void pushTodayCourse() {
        List<Course> courses = courseRepository.findByDate(LocalDate.now());
        courses.forEach(course -> {
            course.getPlanet().getOwners().forEach(user ->
                    notificationService.create(
                            user,
                            messageSource.getMessage("notification.course.dday.title"),
                            messageSource.getMessage("notification.course.dday.body", new String[]{course.getTitle()}),
                            NotificationTarget.COURSE,
                            course.getId()
                    )
            );
        });
    }
}
