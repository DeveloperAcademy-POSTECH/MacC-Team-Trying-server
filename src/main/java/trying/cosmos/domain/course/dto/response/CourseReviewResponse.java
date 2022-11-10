package trying.cosmos.domain.course.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.entity.CourseReview;
import trying.cosmos.domain.course.entity.CourseReviewImage;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseReviewResponse {

    private String writerName;
    private String body;
    private List<String> images;

    public CourseReviewResponse(CourseReview review) {
        this.writerName = review.getWriter().getName();
        this.body = review.getBody();
        this.images = review.getImages().stream().map(CourseReviewImage::getName).collect(Collectors.toList());
    }
}
