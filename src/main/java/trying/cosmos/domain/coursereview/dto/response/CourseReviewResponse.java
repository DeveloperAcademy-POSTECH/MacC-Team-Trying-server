package trying.cosmos.domain.coursereview.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.coursereview.entity.CourseReview;
import trying.cosmos.domain.coursereview.entity.CourseReviewImage;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseReviewResponse {

    private String writerName;
    private String content;
    private List<String> images;

    public CourseReviewResponse(CourseReview review) {
        this.writerName = review.getWriter().getName();
        this.content = review.getContent();
        this.images = review.getImages().stream()
                .map(CourseReviewImage::getName)
                .collect(Collectors.toList());
    }
}
