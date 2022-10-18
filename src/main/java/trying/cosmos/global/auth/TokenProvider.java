package trying.cosmos.global.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import trying.cosmos.domain.user.User;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenProvider {

    private static final String SUBJECT_KEY = "sub";
    private static final String AUTHORITY_KEY = "auth";

    private final Key key;

    public TokenProvider(@Value("${jwt.key}") String key) {
        byte[] bytes = Decoders.BASE64.decode(key);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String getAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim(AUTHORITY_KEY, user.getAuthority())
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Map<String, String> parseToken(String accessToken) {
        Map<String, String> data = new HashMap<>();
        Claims claims = parseClaims(accessToken);
        if (claims.get(SUBJECT_KEY) == null || claims.get(AUTHORITY_KEY) == null) {
            throw new CustomException(ExceptionType.AUTHENTICATION_FAILED);
        }
        data.put(SUBJECT_KEY, claims.getSubject());
        data.put(AUTHORITY_KEY, claims.get(AUTHORITY_KEY, String.class));
        return data;
    }

    public String getSubject(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(SUBJECT_KEY) == null) {
            throw new CustomException(ExceptionType.AUTHENTICATION_FAILED);
        }

        return claims.getSubject();
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key).build()
                    .parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
