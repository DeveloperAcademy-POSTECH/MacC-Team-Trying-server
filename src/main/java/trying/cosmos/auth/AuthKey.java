package trying.cosmos.auth;

public class AuthKey {

    private static ThreadLocal<Long> AuthKey = new ThreadLocal<>();

    public static boolean isAuthenticated() {
        return AuthKey.get() != null;
    }

    public static Long get() {
        return AuthKey.get();
    }

    public static void set(Long id) {
        AuthKey.set(id);
    }

    public static void remove() {
        AuthKey.remove();
    }
}
