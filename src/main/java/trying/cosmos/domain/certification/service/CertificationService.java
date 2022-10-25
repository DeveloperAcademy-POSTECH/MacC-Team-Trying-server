package trying.cosmos.domain.certification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.certification.entity.Certification;
import trying.cosmos.domain.certification.repository.CertificationRepository;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.email.EmailType;
import trying.cosmos.global.utils.email.EmailUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final UserRepository userRepository;
    private final EmailUtils emailUtils;

    public void createCertificationCode(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ExceptionType.EMAIL_DUPLICATED);
        }
        certificationRepository.findByEmail(email).ifPresent(certificationRepository::delete);
        Certification certification = certificationRepository.save(new Certification(email));
        sendCertificationEmail(certification.getEmail(), certification.getCode());
    }

    private void sendCertificationEmail(String email, String code) {
        Map<String, String> model = new HashMap<>();
        model.put("code", code);
        model.put("body1", "계정 보안을 위해 이메일 주소를 인증해주세요.");
        model.put("body2", "다음 인증코드를 10분 이내에 입력해주세요.");
        emailUtils.send(email, "이메일 인증을 완료해주세요", "email-template", EmailType.CERTIFICATION, model);
    }

    public void certificate(String email, String code) {
        Certification certification = certificationRepository.findByEmail(email).orElseThrow();
        certification.certificate(code);
    }

    @Async
    @Scheduled(fixedDelay = 1000 * 60 * 60) // 1시간
    @Transactional
    public void clearCertification() {
        certificationRepository.clearCertifications(LocalDateTime.now());
    }
}
