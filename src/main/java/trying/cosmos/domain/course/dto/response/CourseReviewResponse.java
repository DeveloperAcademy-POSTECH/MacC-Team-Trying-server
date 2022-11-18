package trying.cosmos.domain.course.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.review.dto.response.ReviewResponse;
import trying.cosmos.domain.review.entity.Review;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseReviewResponse {

    private ReviewResponse myReview;
    private ReviewResponse mateReview;

    public CourseReviewResponse(Review myReview, Review mateReview) {
        this.myReview = myReview == null ? null : new ReviewResponse(myReview);
        this.mateReview = mateReview == null ? null : new ReviewResponse(mateReview);
    }
}
