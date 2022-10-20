package trying.cosmos.domain.course;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import trying.cosmos.domain.course.request.TagCreateRequest;
import trying.cosmos.domain.course.response.CourseFindContent;
import trying.cosmos.domain.course.response.CourseFindResponse;
import trying.cosmos.domain.place.PlaceService;
import trying.cosmos.domain.planet.Planet;
import trying.cosmos.domain.planet.PlanetRepository;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.image.S3ImageUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;
    private final CourseRepository courseRepository;
    private final PlaceService placeService;
    private final CourseLikeRepository courseLikeRepository;
    private final S3ImageUtils imageUtils;

    @Transactional
    public Course create(Long userId, Long planetId, String title, String body, Access access, List<TagCreateRequest> tagDto, List<MultipartFile> images) {
        Planet planet = planetRepository.findById(planetId).orElseThrow();
        if (!planet.isOwnedBy(userRepository.findById(userId).orElseThrow())) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        Course course = courseRepository.save(new Course(planet, title, body, access));
        List<Tag> tags = tagDto.stream().map(tag ->
                new Tag(course, placeService.create(tag.getPlace()), tag.getName())
        ).collect(Collectors.toList());

        if (images != null) {
            for (MultipartFile image : images) {
                String imageName = imageUtils.create(image);
                new CourseImage(course, imageName);
            }
        }

        return course;
    }

    public CourseFindResponse find(Long userId, Long courseId) {
        Course course = courseRepository.findByIdWithTagPlace(courseId).orElseThrow();
        if (userId == null) {
            return new CourseFindResponse(course, false);
        }

        User user = userRepository.findById(userId).orElseThrow();
        if (course.getAccess().equals(Access.PRIVATE) && !course.getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        return new CourseFindResponse(course, isLiked(userId, course.getId()));
    }

    public Slice<CourseFindContent> findCourses(Long userId, Pageable pageable) {
        Planet planet = null;
        if (userId != null) {
            User user = userRepository.findById(userId).orElseThrow();
            planet = user.getPlanet();
        }

        Slice<Course> courseSlice = courseRepository.findAll(planet, pageable);
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
        if (!course.getPlanet().isOwnedBy(user) && course.getAccess().equals(Access.PRIVATE)) {
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
}
