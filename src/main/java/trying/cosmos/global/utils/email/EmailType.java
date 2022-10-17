package trying.cosmos.global.utils.email;

public enum EmailType {
    CERTIFICATION("인증코드: "), RESET_PASSWORD("임시 비밀번호: ");

    final String typename;

    EmailType(String typename) {
        this.typename = typename;
    }
}
