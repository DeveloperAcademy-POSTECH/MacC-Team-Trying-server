package trying.cosmos.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.certification.Certification;
import trying.cosmos.domain.certification.CertificationRepository;
import trying.cosmos.domain.certification.CertificationService;
import trying.cosmos.domain.certification.request.CertificateRequest;
import trying.cosmos.domain.certification.request.GenerateCertificationRequest;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static trying.cosmos.docs.utils.ApiDocumentUtils.getDocumentResponse;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@ActiveProfiles("test")
public class CertificationDocsTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @Autowired CertificationService certificationService;
    @Autowired CertificationRepository certificationRepository;

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String EMAIL = "email@gmail.com";

    @Test
    @DisplayName("인증코드 생성")
    void create_certification() throws Exception {
        String content = objectMapper.writeValueAsString(new GenerateCertificationRequest(EMAIL));

        ResultActions actions = mvc.perform(post("/certification")
                .content(content)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("certification/create-certification",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").description("인증받으려는 이메일 주소")
                        )
                ));
    }

    @Test
    @DisplayName("인증코드 확인")
    void certificate() throws Exception {
        certificationService.createCertificationCode(EMAIL);
        Certification certification = certificationRepository.findByEmail(EMAIL).orElseThrow();
        String content = objectMapper.writeValueAsString(new CertificateRequest(EMAIL, certification.getCode()));

        ResultActions actions = mvc.perform(patch("/certification")
                .content(content)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("certification/certificate",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").description("인증받으려는 이메일 주소"),
                                fieldWithPath("code").description("이메일로 받은 인증 코드")
                        )
                ));
    }
}
