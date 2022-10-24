package trying.cosmos.global.auth.entity;

public class AuthKey {

    private static ThreadLocal<Long> authKey = new ThreadLocal<>();
    private static ThreadLocal<Boolean> needAuth = new ThreadLocal<>();

    public static boolean isAuthenticated() {
        return authKey.get() != null;
    }

    public static Long getKey() {
        return authKey.get();
    }

    public static void setKey(Long userId) {
        authKey.set(userId);
    }

    public static void remove() {
        authKey.remove();
        needAuth.remove();
    }

    public static boolean needAuthenticate() {
        return needAuth.get() != null && needAuth.get();
    }

    public static void setNeed(boolean need) {
        needAuth.set(need);
    }
}
