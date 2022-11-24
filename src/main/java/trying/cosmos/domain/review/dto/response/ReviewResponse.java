package trying.cosmos.domain.review.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.review.entity.Review;

import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponse {

    private String writerName;
    private String content;
    private List<String> images;

    public ReviewResponse(Review review, String baseurl) {
        this.writerName = review.getWriter().getName();
        this.content = review.getContent();
        this.images = review.getImages().stream()
                .map(reviewImage -> baseurl + "/images/" + reviewImage.getName())
                .collect(Collectors.toList());
    }
}
