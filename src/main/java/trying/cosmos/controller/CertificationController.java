package trying.cosmos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.controller.request.certification.CertificateRequest;
import trying.cosmos.controller.request.certification.GenerateCertificationRequest;
import trying.cosmos.service.CertificationService;

@RestController
@RequestMapping("/certification")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping
    public void generateCertificateCode(@RequestBody @Validated GenerateCertificationRequest request) {
        certificationService.createCertificationCode(request.getEmail());
    }

    @PatchMapping
    public void certificate(@RequestBody @Validated CertificateRequest request) {
        certificationService.certificate(request.getEmail(), request.getCode());
    }
}
