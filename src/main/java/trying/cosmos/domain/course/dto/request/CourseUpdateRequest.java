package trying.cosmos.domain.course.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CourseUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    private List<TagCreateRequest> tags = new ArrayList<>();
}
