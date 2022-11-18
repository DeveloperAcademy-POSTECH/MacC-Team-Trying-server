package trying.cosmos.domain.review.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.review.entity.Review;
import trying.cosmos.domain.review.entity.ReviewImage;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponse {

    private String writerName;
    private String content;
    private List<String> images;

    public ReviewResponse(Review review) {
        this.writerName = review.getWriter().getName();
        this.content = review.getContent();
        this.images = review.getImages().stream()
                .map(ReviewImage::getName)
                .collect(Collectors.toList());
    }
}
