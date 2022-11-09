package trying.cosmos.domain.course.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import trying.cosmos.domain.course.dto.request.TagCreateRequest;
import trying.cosmos.domain.course.dto.response.CourseFindContent;
import trying.cosmos.domain.course.dto.response.CourseFindResponse;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.CourseImage;
import trying.cosmos.domain.course.entity.CourseLike;
import trying.cosmos.domain.course.repository.CourseLikeRepository;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.image.S3ImageUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;
    private final CourseRepository courseRepository;
    private final CourseLikeRepository courseLikeRepository;
    private final S3ImageUtils imageUtils;
    private final EntityManager em;

    @Transactional
    public Course create(Long userId, Long planetId, String title, String body, List<TagCreateRequest> tagDto, List<MultipartFile> images) {
        Planet planet = planetRepository.searchById(planetId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!planet.isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        Course course = courseRepository.save(new Course(planet, title, body));
        createCourseTag(tagDto, course);
        if (!isEmptyImage(images)) {
            createCourseImage(images, course);
        }

        return course;
    }

    private boolean isEmptyImage(List<MultipartFile> images) {
        return images == null || (images.size() == 1 && images.get(0).getSize() == 0);
    }

    public CourseFindResponse find(Long userId, Long courseId) {
        Course course = courseRepository.searchById(courseId).orElseThrow();
        if (userId == null) {
            return new CourseFindResponse(course, false);
        }

        User user = userRepository.findById(userId).orElseThrow();
        if (!course.getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        return new CourseFindResponse(course, isLiked(userId, course.getId()));
    }

    public Slice<CourseFindContent> findByTitle(Long userId, String title, Pageable pageable) {
        if (userId == null) {
            Slice<Course> courseSlice = courseRepository.searchByName(null, "%" + title + "%", pageable);

            List<CourseFindContent> contents = courseSlice.getContent().stream()
                    .map(course -> new CourseFindContent(course, isLiked(userId, course.getId())))
                    .collect(Collectors.toList());
            return new SliceImpl<>(contents, courseSlice.getPageable(), courseSlice.hasNext());
        } else {
            User user = userRepository.findById(userId).orElseThrow();
            Planet planet = user.getPlanet();
            Slice<Course> courseSlice = courseRepository.searchByName(planet, "%" + title + "%",pageable);

            List<CourseFindContent> contents = courseSlice.getContent().stream()
                    .map(course -> new CourseFindContent(course, isLiked(userId, course.getId())))
                    .collect(Collectors.toList());
            return new SliceImpl<>(contents, courseSlice.getPageable(), courseSlice.hasNext());
        }
    }

    public Slice<CourseFindContent> getFeeds(Long userId, Pageable pageable) {
        Slice<Course> courseSlice = courseRepository.getFeed(userRepository.findById(userId).orElseThrow(), pageable);
        User user = userRepository.findById(userId).orElseThrow();
        List<CourseFindContent> contents = courseSlice.getContent().stream()
                .map(course -> new CourseFindContent(course, isLiked(userId, course.getId())))
                .collect(Collectors.toList());
        return new SliceImpl<>(contents, courseSlice.getPageable(), courseSlice.hasNext());
    }

    private boolean isLiked(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            return false;
        }
        return courseLikeRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    @Transactional
    public void like(Long userId, Long courseId) {
        if (courseLikeRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new CustomException(ExceptionType.DUPLICATED);
        }
        Course course = courseRepository.findById(courseId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!course.getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        courseLikeRepository.save(new CourseLike(userRepository.findById(userId).orElseThrow(), courseRepository.findById(courseId).orElseThrow()));
    }

    @Transactional
    public void unlike(Long userId, Long courseId) {
        if (!courseLikeRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        courseLikeRepository.delete(courseLikeRepository.findByUserIdAndCourseId(userId, courseId).orElseThrow());
    }

    @Transactional
    public Course update(Long userId, Long courseId, String title, String body, List<TagCreateRequest> tagDto, List<MultipartFile> images) {
        Course course = courseRepository.searchById(courseId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (!course.getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        removeCourseImage(course);
        removeCourseTag(course);

        course.update(title, body);
        createCourseTag(tagDto, course);
        createCourseImage(images, course);
        em.flush();
        em.clear();

        return course;
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

    private void createCourseTag(List<TagCreateRequest> tagDto, Course course) {
//        tagDto.forEach(tag -> em.persist(new Tag(course, placeService.create(tag.getPlace()), tag.getName())));
    }

    private void createCourseImage(List<MultipartFile> images, Course course) {
        if (images != null) {
            images.forEach(image -> em.persist(new CourseImage(course, imageUtils.create(image))));
        }
    }

    private void removeCourseTag(Course course) {
        courseRepository.deleteCourseTag(course);
        course.clearTag();
    }

    private void removeCourseImage(Course course) {
        for (CourseImage image : course.getImages()) {
            imageUtils.delete(image.getName());
        }
        courseRepository.deleteCourseImage(course);
        course.clearImage();
    }
}
