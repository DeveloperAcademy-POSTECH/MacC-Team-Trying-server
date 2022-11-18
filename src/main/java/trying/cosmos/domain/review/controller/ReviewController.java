package trying.cosmos.domain.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trying.cosmos.domain.review.dto.response.ReviewCreateResponse;
import trying.cosmos.domain.review.dto.response.ReviewResponse;
import trying.cosmos.domain.review.service.ReviewService;
import trying.cosmos.global.auth.AuthorityOf;
import trying.cosmos.global.auth.entity.AuthKey;
import trying.cosmos.global.auth.entity.Authority;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @AuthorityOf(Authority.USER)
    @PostMapping
    public ReviewCreateResponse createReview(@RequestParam Long courseId, @RequestParam String content, List<MultipartFile> images) {
        return new ReviewCreateResponse(reviewService.create(AuthKey.getKey(), courseId, content, images));
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/{reviewId}")
    public ReviewResponse find(@PathVariable Long reviewId) {
        return new ReviewResponse(reviewService.find(AuthKey.getKey(), reviewId));
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
