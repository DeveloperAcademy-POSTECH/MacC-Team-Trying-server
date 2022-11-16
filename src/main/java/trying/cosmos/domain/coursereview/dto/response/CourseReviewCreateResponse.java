package trying.cosmos.domain.coursereview.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.coursereview.entity.CourseReview;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseReviewCreateResponse {

    private Long reviewId;

    public CourseReviewCreateResponse(CourseReview review) {
        this.reviewId = review.getId();
    }
}
