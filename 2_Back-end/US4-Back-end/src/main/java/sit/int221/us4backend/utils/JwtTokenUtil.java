package sit.int221.us4backend.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.us4backend.model.RefreshRequest;

import javax.servlet.http.HttpServletRequest;

public class JwtTokenUtil implements Serializable {

    private static final JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
    private JwtTokenUtil() { }
    public static JwtTokenUtil getInstance() { return jwtTokenUtil; }

    private static final long serialVersionUID = -2550185165626007488L;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refreshSecret}")
    private String refreshSecret;

    @Value("${jwt.expirationInMs}")
    private int jwtExpirationInMs;

    @Value("${jwt.refreshExpirationInMs}")
    private int refreshExpirationInMs;

    public String getEmailFromRefreshToken(String token) {
        return getClaimFromRefreshToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromRefreshToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().setSigningKey(refreshSecret).parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, email);
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public boolean validateTokenFromHeader(HttpServletRequest request) {
        return validateToken(extractJwtFromHeader(request));
    }

    private String extractJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login user.");
        } catch (ExpiredJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token has expired, please refresh your token.");
        }
    }

    public boolean validateRefreshTokenFromBody(RefreshRequest request) {
        return validateRefreshToken(request.getRefreshToken());
    }

    private boolean validateRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshSecret).parseClaimsJws(refreshToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token, please login again.");
        } catch (ExpiredJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token has expired, please login again.");
        }
    }

    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateRefreshToken(claims, email);
    }

    private String doGenerateRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, refreshSecret).compact();
    }

    public String regenerateToken(String refreshToken) {
        return generateToken(getEmailFromRefreshToken(refreshToken));
    }
    public String regenerateRefreshToken(String refreshToken) {
        return generateToken(getEmailFromRefreshToken(refreshToken));
    }

    public String getEmailFromHeader(HttpServletRequest request) {
        return getEmailFromToken(extractJwtFromHeader(request));
    }

}
