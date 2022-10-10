package trying.cosmos.entity.component;

public enum Authority {
    USER(0), ADMIN(1);

    public final int level;

    private Authority(int level) {
        this.level = level;
    }
}
