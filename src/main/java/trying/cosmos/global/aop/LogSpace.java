package trying.cosmos.global.aop;

public class LogSpace {

    private static ThreadLocal<Integer> depth = new ThreadLocal<>();

    public static void add() {
        depth.set(depth.get() + 1);
    }

    public static void sub() {
        depth.set(depth.get() - 1);
    }

    public static boolean isRoot() {
        return depth.get() == null || depth.get() == 0;
    }

    public static void remove() {
        depth.remove();
    }

    public static String getSpace() {
        if (!isInit()) {
            init();
        }
        return "[" + RequestKeyInterceptor.getRequestKey() + "] " + " ".repeat(depth.get() * 4);
    }

    private static boolean isInit() {
        return depth.get() != null;
    }

    private static void init() {
        depth.set(0);
    }
}
