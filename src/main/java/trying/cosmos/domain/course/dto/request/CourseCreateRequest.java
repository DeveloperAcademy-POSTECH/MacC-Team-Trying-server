package trying.cosmos.domain.course.dto.request;

import lombok.*;
import trying.cosmos.domain.course.entity.Access;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CourseCreateRequest {

    @NotNull
    private Long planetId;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    @NotNull
    private Access access;

    private List<TagCreateRequest> tags = new ArrayList<>();
}
