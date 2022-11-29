package sit.int221.us4backend.utils;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.Function;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.us4backend.model.JwtResponse;
import sit.int221.us4backend.model.MSIPRequest;
import sit.int221.us4backend.model.RefreshRequest;
import sit.int221.us4backend.model.loginResponse;
import sit.int221.us4backend.msip.SigningKeyResolver;

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

    private RSAPublicKey rsaPublicKey;

    public String generateToken(Integer id, String email, Object roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id);
        claims.put("roles", roles);
        return doGenerateToken(claims, email);
    }

    public String generateRefreshToken(Integer id, String email, Object roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id);
        claims.put("roles", roles);
        return doGenerateRefreshToken(claims, email);
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String email) {
        return Jwts.builder().setClaims(claims).setSubject(email).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    private String doGenerateRefreshToken(Map<String, Object> claims, String email) {
        return Jwts.builder().setClaims(claims).setSubject(email).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, refreshSecret).compact();
    }

    public Claims getAllClaimsFromIdToken(String token) {
        SigningKeyResolver signingKeyResolver = null;
        try {
            signingKeyResolver = new SigningKeyResolver("https://login.microsoftonline.com/6f4432dc-20d2-441d-b1db-ac3380ba633d");
        } catch(Exception e) {
            System.out.println("Broke");
        }
        return Jwts.parser().setSigningKeyResolver(signingKeyResolver).parseClaimsJws(token).getBody();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    public Claims getAllClaimsFromRefreshToken(String token) {
        return Jwts.parser().setSigningKey(refreshSecret).parseClaimsJws(token).getBody();
    }

    public ResponseEntity<?> generateFromRefreshToken(RefreshRequest request) {
        Claims claims = getAllClaimsFromRefreshToken(request.getRefreshToken());
        return generateTokenFromClaims(claims);
    }

    public ResponseEntity<?> generateFromIdToken(MSIPRequest request) {
        Claims claims = getAllClaimsFromIdToken(request.getIdToken());
        claims.put("userId", null);
        claims.setSubject((String) claims.get("preferred_username"));

        return generateTokenFromClaims(claims);
    }

    private ResponseEntity<?> generateTokenFromClaims(Claims claims) {
        Integer userId = getUserIdAsInt(claims);
        String name = getNameAsString(claims);
        String email = getEmailAsString(claims);
        ArrayList roles = getRolesAsArrayList(claims);

        final String jwtToken = generateToken(userId, email, roles);
        final String refreshToken = generateRefreshToken(userId, email, roles);
        return ResponseEntity.ok(new loginResponse(userId, name, email, roles, jwtToken, refreshToken));
    }

    public String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return "";
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please login user.");
        } catch (ExpiredJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token has expired, please refresh your token.");
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshSecret).parseClaimsJws(refreshToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token, please login again.");
        } catch (ExpiredJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token has expired, please login again.");
        }
    }

    public Integer getUserIdAsInt(Claims claims) {
        return (Integer) claims.get("userId");
    }
    public String getNameAsString(Claims claims) {
        return (String) claims.get("name");
    }
    public String getEmailAsString(Claims claims) {
        return (String) claims.get(Claims.SUBJECT);
    }
    public ArrayList<String> getRolesAsArrayList(Claims claims) {
        try {
            return new ArrayList((Collection<?>) claims.get("roles"));
        }catch(Exception e) {
            System.out.println("no roles found");
            return new ArrayList();
        }
    }
}
