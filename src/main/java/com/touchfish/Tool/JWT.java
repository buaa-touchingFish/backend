package com.touchfish.Tool;

import com.touchfish.Po.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;

public class JWT {
    private static final String SECRET_KEY = "hangzhuanmoyvxiaofendui";
    private static final long EXPIRATION_TIME = 86400000; // 过期时间为一天，单位为毫秒

    public static String generateJwtToken(String subject) { //生成jwt串
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        String jwtToken = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        return jwtToken;
    }

    public static String extractUsername(String jwtToken) { //解析出username
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken).getBody();
        return claims.getSubject();
    }

}
