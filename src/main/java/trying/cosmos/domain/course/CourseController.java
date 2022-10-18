package trying.cosmos.domain.course;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.domain.course.request.CourseCreateRequest;
import trying.cosmos.domain.course.response.CourseCreateResponse;
import trying.cosmos.domain.course.response.CourseFindResponse;
import trying.cosmos.domain.course.response.CourseListFindResponse;
import trying.cosmos.global.auth.AuthKey;
import trying.cosmos.global.auth.Authority;
import trying.cosmos.global.auth.AuthorityOf;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @AuthorityOf(Authority.USER)
    @PostMapping
    public CourseCreateResponse create(@RequestBody @Validated CourseCreateRequest request) {
        return new CourseCreateResponse(courseService.create(
                AuthKey.getKey(),
                request.getPlanetId(),
                request.getTitle(),
                request.getBody(),
                request.getAccess(),
                request.getTags())
        );
    }

    @GetMapping("/{courseId}")
    public CourseFindResponse find(@PathVariable Long courseId) {
        return courseService.find(AuthKey.getKey(), courseId);
    }

    @GetMapping
    public CourseListFindResponse findList(Pageable pageable) {
        return new CourseListFindResponse(courseService.findCourses(AuthKey.getKey(), pageable));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/{courseId}/like")
    public void like(@PathVariable Long courseId) {
        courseService.like(AuthKey.getKey(), courseId);
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{courseId}/like")
    public void dislike(@PathVariable Long courseId) {
        courseService.dislike(AuthKey.getKey(), courseId);
    }
}
