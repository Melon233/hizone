package com.example.fenta.utility;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class Utility {

    private static String secret = "secret";

    public static String generateToken(int userId) {
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

    public static int extractUserId(String token) {
        return JWT.decode(token).getClaim("user_id").asInt();
    }

    public static boolean validateEmail(String email) {
        String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        if (email.matches(regex)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validateNickname(String nickname) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateNickname'");
    }




}
