package trying.cosmos.controller.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserUpdateControllerRequest {

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]{2,8}", message = "닉네임은 영어와 숫자로 이루어진 2~8자리 문자열입니다.")
    private String name;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]{8,12}", message = "비밀번호는 영어와 숫자로 이루어진 8~12자리 문자열입니다.")
    private String password;
}
