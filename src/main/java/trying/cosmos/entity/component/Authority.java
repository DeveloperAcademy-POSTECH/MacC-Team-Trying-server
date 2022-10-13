package trying.cosmos.entity.component;

public enum Authority {
    USER(0), ADMIN(1);

    public final int level;

    Authority(int level) {
        this.level = level;
    }
}
