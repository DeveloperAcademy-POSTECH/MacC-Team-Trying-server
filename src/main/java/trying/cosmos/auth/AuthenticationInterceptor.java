package trying.cosmos.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import trying.cosmos.entity.Member;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.repository.MemberRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String ACCESS_TOKEN_HEADER = "access_token";

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AuthorityOf annotation = ((HandlerMethod) handler).getMethodAnnotation(AuthorityOf.class);
        if (annotation == null) {
            return true;
        }

        String token = request.getHeader(ACCESS_TOKEN_HEADER);
        if (token == null || !tokenProvider.validateToken(token)) {
            throw new CustomException(ExceptionType.INVALID_TOKEN);
        }

        Member member = memberRepository.findByEmail(tokenProvider.getSubject(token))
                .orElseThrow(() -> new CustomException(ExceptionType.NO_DATA));

        Authority authority = annotation.value();
        if (member.getAuthority().level < authority.level) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        LoginMember.setLoginMember(member);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginMember.remove();
    }
}
