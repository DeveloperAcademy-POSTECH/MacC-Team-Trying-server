package trying.cosmos.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserFindContent {

    private String name;
    private String email;

    public UserFindContent(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
