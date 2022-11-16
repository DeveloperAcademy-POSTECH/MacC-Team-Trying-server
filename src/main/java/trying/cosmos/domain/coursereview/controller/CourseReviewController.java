package trying.cosmos.domain.coursereview.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trying.cosmos.domain.coursereview.dto.response.CourseReviewCreateResponse;
import trying.cosmos.domain.coursereview.dto.response.CourseReviewResponse;
import trying.cosmos.domain.coursereview.service.CourseReviewService;
import trying.cosmos.global.auth.AuthorityOf;
import trying.cosmos.global.auth.entity.AuthKey;
import trying.cosmos.global.auth.entity.Authority;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService reviewService;

    @AuthorityOf(Authority.USER)
    @PostMapping
    public CourseReviewCreateResponse createReview(@RequestParam Long courseId, @RequestParam String content, List<MultipartFile> images) {
        return new CourseReviewCreateResponse(reviewService.create(AuthKey.getKey(), courseId, content, images));
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/{reviewId}")
    public CourseReviewResponse find(@PathVariable Long reviewId) {
        return new CourseReviewResponse(reviewService.find(AuthKey.getKey(), reviewId));
    }

    @AuthorityOf(Authority.USER)
    @PutMapping("/{reviewId}")
    public void updateReview(@PathVariable Long reviewId, @RequestParam String content, List<MultipartFile> images) {
        reviewService.update(AuthKey.getKey(), reviewId, content, images);
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        reviewService.delete(AuthKey.getKey(), reviewId);
    }
}
