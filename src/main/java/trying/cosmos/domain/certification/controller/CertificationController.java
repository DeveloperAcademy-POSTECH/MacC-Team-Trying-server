package trying.cosmos.domain.certification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.domain.certification.dto.request.CertificateRequest;
import trying.cosmos.domain.certification.dto.request.GenerateCertificationRequest;
import trying.cosmos.domain.certification.service.CertificationService;

@RestController
@RequestMapping("/certification")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping
    public void generate(@RequestBody @Validated GenerateCertificationRequest request) {
        certificationService.generate(request.getEmail());
    }

    @PatchMapping
    public void certificate(@RequestBody @Validated CertificateRequest request) {
        certificationService.certificate(request.getEmail(), request.getCode());
    }
}
