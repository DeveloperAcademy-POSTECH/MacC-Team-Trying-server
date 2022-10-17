package trying.cosmos.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import trying.cosmos.entity.User;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;

import java.security.Key;

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
