package trying.cosmos.domain.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserLoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String deviceToken;
}
