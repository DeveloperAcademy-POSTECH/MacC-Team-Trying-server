package trying.cosmos.domain.course.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import trying.cosmos.domain.course.dto.request.CoursePlaceRequest;
import trying.cosmos.domain.course.dto.response.CourseDateResponse;
import trying.cosmos.domain.course.dto.response.CourseFindContent;
import trying.cosmos.domain.course.dto.response.CourseFindResponse;
import trying.cosmos.domain.course.entity.*;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.course.repository.CourseReviewLikeRepository;
import trying.cosmos.domain.place.repository.PlaceRepository;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.image.ImageUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;
    private final CourseRepository courseRepository;
    private final PlaceRepository placeRepository;
    private final CourseReviewLikeRepository courseReviewLikeRepository;
    private final ImageUtils imageUtils;
    private final EntityManager em;

    @Transactional
    public Course create(Long userId, Long planetId, String title, LocalDate date, List<CoursePlaceRequest> placeRequests) {
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!planet.isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        if (courseRepository.searchByDate(planet, date).isPresent()) {
            throw new CustomException(ExceptionType.DUPLICATED);
        }
        Course course = courseRepository.save(new Course(planet, title, date));

        placeRequests.forEach(p ->
                new CoursePlace(course, placeRepository.findById(p.getPlaceId()).orElseThrow(), p.getMemo())
        );

        return course;
    }

    public CourseFindResponse find(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();
        return new CourseFindResponse(course, isLiked(userId, course.getId()));
    }

    public CourseFindResponse findByDate(Long userId, LocalDate date) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchByDate(user.getPlanet(), date).orElseThrow();
        return new CourseFindResponse(course, isLiked(userId, course.getId()));
    }

    public CourseDateResponse getCourseDates(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return new CourseDateResponse(courseRepository.searchAllDates(user.getPlanet()));
    }

    public Slice<CourseFindContent> findByTitle(Long userId, String title, Pageable pageable) {User user = userRepository.findById(userId).orElseThrow();
        Planet planet = user.getPlanet();
        Slice<Course> courseSlice = courseRepository.searchByName(planet, "%" + title + "%",pageable);

        List<CourseFindContent> contents = courseSlice.getContent().stream()
                .map(course -> new CourseFindContent(course, isLiked(userId, course.getId())))
                .collect(Collectors.toList());
        return new SliceImpl<>(contents, courseSlice.getPageable(), courseSlice.hasNext());
    }

    public Slice<CourseFindContent> getFeeds(Long userId, Pageable pageable) {
        Slice<Course> courseSlice = courseRepository.getFeed(userRepository.findById(userId).orElseThrow(), pageable);
        List<CourseFindContent> contents = courseSlice.getContent().stream()
                .map(course -> new CourseFindContent(course, isLiked(userId, course.getId())))
                .collect(Collectors.toList());
        return new SliceImpl<>(contents, courseSlice.getPageable(), courseSlice.hasNext());
    }

    private boolean isLiked(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            return false;
        }
        return courseReviewLikeRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    @Transactional
    public void like(Long userId, Long courseId) {
        if (courseReviewLikeRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new CustomException(ExceptionType.DUPLICATED);
        }
        Course course = courseRepository.findById(courseId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!course.getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        courseReviewLikeRepository.save(new CourseReviewLike(userRepository.findById(userId).orElseThrow(), courseRepository.findById(courseId).orElseThrow()));
    }

    @Transactional
    public void unlike(Long userId, Long courseId) {
        if (!courseReviewLikeRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        courseReviewLikeRepository.delete(courseReviewLikeRepository.findByUserIdAndCourseId(userId, courseId).orElseThrow());
    }

    @Transactional
    public void createReview(Long userId, Long courseId, String body, List<MultipartFile> images) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();
        if (!course.getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }
        if (course.isReviewed(user)) {
            throw new CustomException(ExceptionType.DUPLICATED);
        }

        CourseReview review = new CourseReview(user, course, body);
        images.forEach(image -> createImage(review, image));
    }

    public CourseReview findMyReview(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!course.getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        return course.getReview(user).orElseThrow();
    }

    public CourseReview findMateReview(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!course.getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        return course.getReview(user.getMate()).orElseThrow();
    }

    @Transactional
    public void updateReview(Long userId, Long courseId, String body, List<MultipartFile> images) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();

        Optional<CourseReview> myReview = course.getReview(user);
        if (myReview.isEmpty()) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        myReview.get().update(body);
        myReview.get().getImages().forEach(this::removeExistImages);
        myReview.get().getImages().clear();

        images.forEach(image -> createImage(myReview.get(), image));
    }

    @Transactional
    public void deleteReview(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();
        if (!course.getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        Optional<CourseReview> myReview = course.getReview(user);
        if (myReview.isEmpty()) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        course.getReviews().remove(myReview.get());
        em.remove(myReview.get());
    }

    private void removeExistImages(CourseReviewImage image) {
        imageUtils.delete(image.getName());
        em.remove(image);
    }

    private void createImage(CourseReview review, MultipartFile image) {
        String name = imageUtils.create(image);
        new CourseReviewImage(review, name);
    }

    @Transactional
    public Course update(Long userId, Long courseId, String title, LocalDate date, List<CoursePlaceRequest> placeRequests) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();
        if (!course.getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

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
        Course course = courseRepository.findById(courseId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!course.getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }
        course.delete();
    }
}
