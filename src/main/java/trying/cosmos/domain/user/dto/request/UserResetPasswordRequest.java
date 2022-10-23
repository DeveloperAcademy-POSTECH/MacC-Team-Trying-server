package trying.cosmos.domain.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserResetPasswordRequest {

    @NotBlank
    private String email;
}
