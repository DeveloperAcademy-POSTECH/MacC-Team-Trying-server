package trying.cosmos.domain.course.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.entity.Access;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CourseUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    @NotNull
    private Access access;

    private List<TagCreateRequest> tags = new ArrayList<>();
}
