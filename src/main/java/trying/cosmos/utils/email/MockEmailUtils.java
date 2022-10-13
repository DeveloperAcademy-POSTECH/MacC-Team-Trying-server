package trying.cosmos.utils.email;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MockEmailUtils implements EmailUtils {

    @Override
    public void send(String to, String subject, String template, EmailType type, Map<String, String> model) {
    }
}
