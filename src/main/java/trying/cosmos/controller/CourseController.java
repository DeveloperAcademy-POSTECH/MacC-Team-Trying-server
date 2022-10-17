package trying.cosmos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.auth.AuthKey;
import trying.cosmos.auth.AuthorityOf;
import trying.cosmos.controller.request.course.CourseCreateRequest;
import trying.cosmos.controller.response.course.CourseCreateResponse;
import trying.cosmos.controller.response.course.CourseFindResponse;
import trying.cosmos.controller.response.course.CourseListFindResponse;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.service.CourseService;

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

    @AuthorityOf(Authority.USER)
    @GetMapping("/{id}")
    public CourseFindResponse find(@PathVariable Long id) {
        return new CourseFindResponse(courseService.find(AuthKey.get(), id), courseService.isLiked(AuthKey.get(), id));
    }

    @AuthorityOf(Authority.USER)
    @GetMapping
    public CourseListFindResponse findList(Pageable pageable) {
        return new CourseListFindResponse(courseService.findFeeds(AuthKey.get(), pageable));
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
