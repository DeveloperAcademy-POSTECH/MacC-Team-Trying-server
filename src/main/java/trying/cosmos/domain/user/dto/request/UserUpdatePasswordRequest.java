package trying.cosmos.domain.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserUpdatePasswordRequest {

    @NotBlank
    private String previousPassword;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*]{8,16}", message = "비밀번호는 영어와 숫자, 특수문자로 이루어진 8~16자리 문자열입니다.")
    private String updatePassword;
}
