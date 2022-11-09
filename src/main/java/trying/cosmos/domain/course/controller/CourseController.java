package trying.cosmos.domain.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trying.cosmos.domain.course.dto.request.CourseCreateRequest;
import trying.cosmos.domain.course.dto.request.CourseUpdateRequest;
import trying.cosmos.domain.course.dto.response.CourseCreateResponse;
import trying.cosmos.domain.course.dto.response.CourseFindResponse;
import trying.cosmos.domain.course.dto.response.CourseListFindResponse;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.global.auth.AuthorityOf;
import trying.cosmos.global.auth.entity.AuthKey;
import trying.cosmos.global.auth.entity.Authority;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @AuthorityOf(Authority.USER)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public CourseCreateResponse create(@RequestPart(name = "data") @Validated CourseCreateRequest request, List<MultipartFile> images) {
        return new CourseCreateResponse(courseService.create(
                AuthKey.getKey(),
                request.getPlanetId(),
                request.getTitle(),
                request.getBody(),
                request.getTags(),
                images
        ));
    }

    @GetMapping("/{courseId}")
    public CourseFindResponse find(@PathVariable Long courseId) {
        return courseService.find(AuthKey.getKey(), courseId);
    }

    @GetMapping
    public CourseListFindResponse findByName(@RequestParam(required = false, defaultValue = "") String query, Pageable pageable) {
        return new CourseListFindResponse(courseService.findByTitle(AuthKey.getKey(), query, pageable));
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/feed")
    public CourseListFindResponse getFeed(Pageable pageable) {
        return new CourseListFindResponse(courseService.getFeeds(AuthKey.getKey(), pageable));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/{courseId}/like")
    public void like(@PathVariable Long courseId) {
        courseService.like(AuthKey.getKey(), courseId);
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{courseId}/like")
    public void dislike(@PathVariable Long courseId) {
        courseService.unlike(AuthKey.getKey(), courseId);
    }

    @AuthorityOf(Authority.USER)
    @PutMapping("/{courseId}")
    public CourseCreateResponse update(@PathVariable Long courseId, @RequestPart(name = "data") @Validated CourseUpdateRequest request, List<MultipartFile> images) {
        return new CourseCreateResponse(courseService.update(AuthKey.getKey(), courseId, request.getTitle(), request.getBody(), request.getTags(), images));
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{courseId}")
    public void delete(@PathVariable Long courseId) {
        courseService.delete(AuthKey.getKey(), courseId);
    }
}
