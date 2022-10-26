package trying.cosmos.domain.certification.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GenerateCertificationRequest {

    @NotBlank
    @Email(message = "이메일 형식이 아닙니다.")
    String email;
}
