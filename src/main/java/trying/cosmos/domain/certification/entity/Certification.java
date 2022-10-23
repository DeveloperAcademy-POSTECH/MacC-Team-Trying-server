package trying.cosmos.domain.certification.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import javax.persistence.*;
import java.time.LocalDateTime;

@ToString
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Certification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String code;

    private boolean isCertified;

    private LocalDateTime expiredDate;

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRED_TIME =  60 * 10; //10분

    // Constructor
    public Certification(String email) {
        this.email = email;
        this.code = RandomStringUtils.random(CODE_LENGTH, true, true);
        this.isCertified = false;
        this.expiredDate = LocalDateTime.now().plusSeconds(EXPIRED_TIME);
    }

    public void certificate(String code) {
        if (isExpired() || isWrongCode(code)) {
            throw new CustomException(ExceptionType.CERTIFICATION_FAILED);
        }
        this.isCertified = true;
    }

    private boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredDate);
    }

    private boolean isWrongCode(String code) {
        return !this.code.equals(code);
    }
}
