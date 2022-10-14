package trying.cosmos.entity.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.User;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Certification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String code;

    private LocalDateTime expiredDate;

    private static final int LENGTH = 6;
    private static final int EXPIRED_TIME =  60 * 10; //10분

    // Constructor
    public Certification(User user, String code) {
        this.user = user;
        this.code = code;
        this.expiredDate = LocalDateTime.now().plusSeconds(EXPIRED_TIME);
    }

    // Convenient Method
    public static int getLength() {
        return LENGTH;
    }

    public void certificate(String code) {
        if (isExpired() || isWrongCode(code)) {
            throw new CustomException(ExceptionType.CERTIFICATION_FAILED);
        }
        user.certificate();
    }

    private boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredDate);
    }

    private boolean isWrongCode(String code) {
        return !this.code.equals(code);
    }
}
