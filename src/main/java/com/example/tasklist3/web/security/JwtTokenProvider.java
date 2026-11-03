package com.example.tasklist3.web.security;

import com.example.tasklist3.domain.exception.AccessDeniedException;
import com.example.tasklist3.domain.user.Role;
import com.example.tasklist3.domain.user.User;
import com.example.tasklist3.service.UserService;
import com.example.tasklist3.service.props.JwtProperties;
import com.example.tasklist3.web.dto.auth.JwtResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** <a href="https://medium.com/@frankpythagore/upgrading-your-jwt-implementation-in-java-a-guide-to-the-new-io-jsonwebtoken-api-ab30234d5979">...</a> */

// TODO check newer code works
// https://github.com/SpringBootCourses/tasklist/blob/main/src/main/java/com/example/tasklist/web/security/JwtTokenProvider.java

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;  // хранит данные из application.yaml

    private final UserDetailsService userDetailsService;
    private final UserService userService;
    // private Key key;
    private SecretKey key;

    @PostConstruct  // вызывается после конструктора - к этому моменту три поля (userDetailsService + userService + Key) уже заавтовайрены, @Autowired происходит в конструкторе
    public void init() {
         this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());

        //from articles: https://medium.com/@frankpythagore/upgrading-your-jwt-implementation-in-java-a-guide-to-the-new-io-jsonwebtoken-api-ab30234d5979
//         this.key = new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        // или
        // getSignInKey()-method:
        //   byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        //   return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(
            final Long userId,
            final String username,
            final Set<Role> roles
    ) {
//        Claims claims = /*(Claims)*/ Jwts.claims().setSubject(username);
        Claims claims = Jwts.claims()
                .subject(username)
                .add("id", userId)
                .add("roles", resolveRoles(roles))
                .build();

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperties.getAccess()); // когда токен умрёт
//        Instant validity = Instant.now()
//                .plus(jwtProperties.getAccess(), ChronoUnit.HOURS);

        // from video
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(validity)
//                .signWith(key)
//                .compact();

        // for newer versions
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)  // new Date(System.currentTimeMillis())
                .expiration(validity)   // new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10) // 10 hours
                // .expiration(Date.from(validity))
                .signWith(key)  // getSignInKey(), Jwts.SIG.HS256
                .compact();
    }

    // получаем список ролей из enum в виде List[]
    private List<String> resolveRoles(
            final Set<Role> roles
    ) {
        return roles.stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public String createRefreshToken(
            final Long userId,
            final String username
    ) {
//        Claims claims = /*(Claims)*/ Jwts.claims().setSubject(username);
        Claims claims = Jwts.claims()
                .subject(username)
                .add("id", userId)
                .build();

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperties.getRefresh());
//        Instant validity = Instant.now()
//                .plus(jwtProperties.getRefresh(), ChronoUnit.DAYS);

//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(validity)
//                .signWith(key)
//                .compact();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)  // new Date(System.currentTimeMillis())
                .expiration(validity)   // new Date(System.currentTimeMillis() + 1000* 60 * 60 *10) // 10 hours
                // .expiration(Date.from(validity))
                .signWith(key)  // getSignInKey(), Jwts.SIG.HS256
                .compact();
    }

    public JwtResponse refreshUserTokens(
            final String refreshToken
    ) {
        JwtResponse jwtResponse = new JwtResponse();

        if (!validateToken(refreshToken)) {
            throw new AccessDeniedException();
        }

        Long userId = Long.valueOf(getId(refreshToken));
        User user = userService.getById(userId);
        jwtResponse.setId(userId);
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setAccessToken(createAccessToken(userId, user.getUsername(), user.getRoles()));
        jwtResponse.setRefreshToken(createRefreshToken(userId, user.getUsername()));

        return jwtResponse;
    }

    public boolean validateToken(
            final String token
    ) {
        Jws<Claims> claims = Jwts
//             .parserBuilder()
            .parser()
                // .setSigningKey(key)
                .verifyWith(key)
                .build()
                // .parseClaimsJws(token)
                .parseSignedClaims(token);

        return claims
//                .getBody()
                .getPayload()
                .getExpiration()
                .after(new Date());
    }

    private String getId(
            final String token
    ) {
        return Jwts
                // .parserBuilder()
                .parser()
//                 .setSigningKey(key)
                .verifyWith(key)
                .build()
//                 .parseClaimsJws(token)
                .parseSignedClaims(token)
                //.getBody()
                .getPayload()
                .get("id", String.class);
//                .toString();
    }

    private String getUsername(
            final String token
    ) {
        return Jwts
                // .parserBuilder()
                .parser()
//                .setSigningKey(key)
                .verifyWith(key)
                .build()
//                .parseClaimsJws(token)
                .parseSignedClaims(token)
//                .getBody()
                .getPayload()
                .getSubject();
    }

    public Authentication getAuthentication(
            final String token
    ) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }

}
