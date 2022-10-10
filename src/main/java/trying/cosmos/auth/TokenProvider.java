package trying.cosmos.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import trying.cosmos.entity.Member;

import java.security.Key;

public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";

    private final Key key;

    public TokenProvider(@Value("${jwt.key}") String key) {
        byte[] bytes = Decoders.BASE64.decode(key);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String getAccessToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getEmail())
                .claim(AUTHORITIES_KEY, member.getAuthority().toString())
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getClaims(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("잘못된 토큰입니다.");
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
