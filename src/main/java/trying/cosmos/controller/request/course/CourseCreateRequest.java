package trying.cosmos.controller.request.course;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.component.Access;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
