package trying.cosmos.domain.certification.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CertificateRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String code;
}
