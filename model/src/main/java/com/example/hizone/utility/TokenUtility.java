package com.example.hizone.utility;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class TokenUtility {

    private static String secret = "secret";

    public static String generateToken(Long userId) {
        return JWT.create().withClaim("user_id", userId).withIssuedAt(new Date()).sign(Algorithm.HMAC256(secret));
    }

    public static boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Long extractUserId(String token) {
        return JWT.decode(token).getClaim("user_id").asLong();
    }

    public static boolean validateEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        System.out.println("email: " + email);
        if (email.matches(regex)) {
            return true;
        } else {
            return false;
        }
    }
}
