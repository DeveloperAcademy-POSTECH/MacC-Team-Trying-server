package trying.cosmos.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import trying.cosmos.docs.utils.RestDocsConfiguration;
import trying.cosmos.domain.certification.Certification;
import trying.cosmos.domain.certification.CertificationRepository;
import trying.cosmos.domain.certification.CertificationService;
import trying.cosmos.domain.certification.request.CertificateRequest;
import trying.cosmos.domain.certification.request.GenerateCertificationRequest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith({RestDocumentationExtension.class})
@Transactional
@ActiveProfiles("test")
public class CertificationDocsTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CertificationService certificationService;
    @Autowired
    CertificationRepository certificationRepository;

    @Autowired
    RestDocumentationResultHandler restDocs;

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String EMAIL = "email@gmail.com";

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider, WebApplicationContext context){
        this.mvc= MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(restDocs)
                .addFilters(new CharacterEncodingFilter("UTF-8",true))
                .build();
    }

    @Test
    @DisplayName("인증코드 생성")
    void create_certification() throws Exception {
        String content = objectMapper.writeValueAsString(new GenerateCertificationRequest(EMAIL));

        ResultActions actions = mvc.perform(post("/certification")
                        .content(content)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("email")
                                .type(STRING)
                                .description("인증받으려는 이메일 주소")
                                .attributes(key("constraint").value("이메일 형식"))
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

        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("email")
                                .type(STRING)
                                .description("인증받으려는 이메일 주소")
                                .attributes(key("constraint").value("이메일 형식")),
                        fieldWithPath("code")
                                .type(STRING)
                                .description("이메일로 받은 인증 코드")
                )
        ));
    }
}


