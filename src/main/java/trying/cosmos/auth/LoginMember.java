package trying.cosmos.auth;

import trying.cosmos.entity.Member;

public class LoginMember {

    private static ThreadLocal<Member> loginMember = new ThreadLocal<>();

    public static Member getLoginMember() {
        return loginMember.get();
    }

    public static void setLoginMember(Member member) {
        loginMember.set(member);
    }

    public static void remove() {
        loginMember.remove();
    }
}
