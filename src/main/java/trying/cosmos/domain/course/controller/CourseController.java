package trying.cosmos.domain.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trying.cosmos.domain.course.dto.request.CourseCreateRequest;
import trying.cosmos.domain.course.dto.request.CourseUpdateRequest;
import trying.cosmos.domain.course.dto.response.*;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.global.auth.AuthorityOf;
import trying.cosmos.global.auth.entity.AuthKey;
import trying.cosmos.global.auth.entity.Authority;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.DateUtils;

import java.util.List;

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
                request.getTitle(),
                DateUtils.stringToDate(request.getDate()),
                request.getPlaces()
        ));
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/{courseId}")
    public CourseFindResponse find(@PathVariable Long courseId) {
        return courseService.find(AuthKey.getKey(), courseId);
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/dates/{date}")
    public CourseFindResponse findByDate(@PathVariable String date) {
        return courseService.findByDate(AuthKey.getKey(), DateUtils.stringToDate(date));
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/dates")
    public CourseDateResponse findCourseDates(@RequestParam String start,
                                              @RequestParam String end) {
        return courseService.findCourseDates(
                AuthKey.getKey(),
                DateUtils.stringToDate(start),
                DateUtils.stringToDate(end)
        );
    }

    @AuthorityOf(Authority.USER)
    @GetMapping
    public CourseListFindResponse findList(@RequestParam(required = false, defaultValue = "") String query,
                                           @RequestParam(required = false, defaultValue = "false") boolean likeonly,
                                           Pageable pageable) {
        return new CourseListFindResponse(courseService.findList(AuthKey.getKey(), query, likeonly, pageable));
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/log")
    public CourseListFindResponse findLogs(Pageable pageable) {
        return new CourseListFindResponse(courseService.findLogs(AuthKey.getKey(), pageable));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/{courseId}/like")
    public void like(@PathVariable Long courseId) {
        courseService.like(AuthKey.getKey(), courseId);
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{courseId}/like")
    public void unlike(@PathVariable Long courseId) {
        courseService.unlike(AuthKey.getKey(), courseId);
    }

    @AuthorityOf(Authority.USER)
    @PutMapping("/{courseId}")
    public CourseCreateResponse update(@PathVariable Long courseId, @RequestBody @Validated CourseUpdateRequest request) {
        return new CourseCreateResponse(courseService.update(
                AuthKey.getKey(),
                courseId,
                request.getTitle(),
                DateUtils.stringToDate(request.getDate()),
                request.getPlaces()
        ));
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{courseId}")
    public void delete(@PathVariable Long courseId) {
        courseService.delete(AuthKey.getKey(), courseId);
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/{courseId}/review")
    public void createReview(@PathVariable Long courseId, @RequestParam String content, List<MultipartFile> images) {
        courseService.createReview(AuthKey.getKey(), courseId, content, images);
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/{courseId}/review")
    public CourseReviewResponse findReview(@PathVariable Long courseId, @RequestParam String writer) {
        if (writer.equals("me")) {
            return new CourseReviewResponse(courseService.findMyReview(AuthKey.getKey(), courseId));
        } else if (writer.equals("mate")) {
            return new CourseReviewResponse(courseService.findMateReview(AuthKey.getKey(), courseId));
        } else {
            throw new CustomException(ExceptionType.INVALID_PARAMETER);
        }
    }

    @AuthorityOf(Authority.USER)
    @PutMapping("/{courseId}/review")
    public void updateReview(@PathVariable Long courseId, @RequestParam String content, List<MultipartFile> images) {
        courseService.updateReview(AuthKey.getKey(), courseId, content, images);
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{courseId}/review")
    public void deleteReview(@PathVariable Long courseId) {
        courseService.deleteReview(AuthKey.getKey(), courseId);
    }
}
