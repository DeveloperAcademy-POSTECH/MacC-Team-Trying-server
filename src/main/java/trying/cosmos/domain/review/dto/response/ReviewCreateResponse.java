package trying.cosmos.domain.review.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.review.entity.Review;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewCreateResponse {

    private Long reviewId;

    public ReviewCreateResponse(Review review) {
        this.reviewId = review.getId();
    }
}
