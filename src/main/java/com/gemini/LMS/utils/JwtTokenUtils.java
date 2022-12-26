package com.gemini.LMS.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gemini.LMS.model.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author va.gupta
 * class to generate and parse the jwt token
 */
@Component
public class JwtTokenUtils {

    private static final String USER_ROLE = "user_role";
    private static final String ID = "id";
    private static final String USER_EMAIL = "user_email";
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.issuer}")
    private String jwtIssuer;

    /**
     * method to generate the jwt token
     *
     * @param user into
     * @return jwt token
     */
    public String generateToken(final UserInfo user) {
        Algorithm algorithm = Algorithm.HMAC512(secret);
        return JWT.create().withIssuer(jwtIssuer)
                .withSubject(user.getFullName())
                .withClaim(ID, user.getUsername())
                .withClaim(USER_ROLE, user.getRole())
                .withClaim(USER_EMAIL, user.getEmail())
                .sign(algorithm);
    }

    /**
     * method to parse the jwt token
     *
     * @param token is jwt token
     * @return user info if token is valid else null
     */
    public UserInfo parseToken(final String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(jwtIssuer).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            UserInfo user = new UserInfo();
            user.setUsername(decodedJWT.getClaim(ID).asString());
            user.setEmail(decodedJWT.getClaim(USER_EMAIL).asString());
            user.setRole(decodedJWT.getClaim(USER_ROLE).asString());
            return user;
        } catch (JWTVerificationException e) {
            return null;
        }
    }

}
