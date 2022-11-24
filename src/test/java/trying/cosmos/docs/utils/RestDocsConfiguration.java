package trying.cosmos.docs.utils;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

@TestConfiguration
public class RestDocsConfiguration {
    @Bean
    public RestDocumentationResultHandler restDocsMockMvcConfigurationCustomizer() {
        return MockMvcRestDocumentation.document("{class-name}/{method-name}",
                Preprocessors.preprocessRequest(
                        modifyUris()
                                .scheme("http")
                                .host("localhost")
                                .port(3059),
                        prettyPrint()
                ),
                preprocessResponse(prettyPrint()));
    }

    public static Attributes.Attribute constraints(String value) {
        return new Attributes.Attribute("constraints", value);
    }
}