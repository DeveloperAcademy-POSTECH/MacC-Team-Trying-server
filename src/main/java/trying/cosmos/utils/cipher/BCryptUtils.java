package trying.cosmos.utils.cipher;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BCryptUtils {

    public static String encrypt(String password) {
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    public static boolean isMatch(String password, String hashed) {
        return BCrypt.checkpw(password,hashed);
    }
}
