package trying.cosmos.global.utils.email;

import java.util.Map;

public interface EmailUtils {

    void send(String to, String subject, String template, EmailType type, Map<String, String> model);
}
