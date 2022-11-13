package trying.cosmos.global.utils;

import org.mindrot.jbcrypt.BCrypt;

public interface BCryptUtils {

    static String encrypt(String password) {
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    static boolean isMatch(String password, String hashed) {
        return BCrypt.checkpw(password,hashed);
    }
}
