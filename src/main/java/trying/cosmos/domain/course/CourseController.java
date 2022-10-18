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
                AuthKey.get(),
                request.getPlanetId(),
                request.getTitle(),
                request.getBody(),
                request.getAccess(),
                request.getTags())
        );
    }

    @GetMapping("/{id}")
    public CourseFindResponse find(@PathVariable Long id) {
        return courseService.find(AuthKey.isAuthenticated() ? AuthKey.get() : null, id);
    }

    @GetMapping
    public CourseListFindResponse findList(Pageable pageable) {
        return new CourseListFindResponse(courseService.findCourses(AuthKey.isAuthenticated() ? AuthKey.get() : null, pageable));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/{id}/like")
    public void like(@PathVariable Long id) {
        courseService.like(AuthKey.get(), id);
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{id}/like")
    public void dislike(@PathVariable Long id) {
        courseService.dislike(AuthKey.get(), id);
    }
}
