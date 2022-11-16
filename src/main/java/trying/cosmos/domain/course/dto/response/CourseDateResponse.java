package trying.cosmos.domain.course.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.global.utils.DateUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseDateResponse {

    List<String> dates;

    public CourseDateResponse(List<LocalDate> dates) {
        this.dates = dates.stream()
                .map(DateUtils::getFormattedDate)
                .collect(Collectors.toList());
    }
}
