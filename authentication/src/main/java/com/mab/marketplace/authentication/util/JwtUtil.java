package com.mab.marketplace.authentication.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * Класс для jwt-токенов
 */
@Slf4j
@Component
public class JwtUtil {

    // Ключ для генерации и расшифровки токена
    private final String SIGN = "MarK3tPllllllac3SecretSiignCCC00de0002SYSTeM";

    /**
     * Создание нового токена
     * Длительность ~ 57,8 дней
     *
     * @param claims  информация, содержащаяся в токене
     * @return  токен
     */
    public String generateToken(Claims claims){
        long nowMillis = System.currentTimeMillis();
        long expirationMillis = nowMillis + 5_000_000_000L;
        Date exp = new Date(expirationMillis);

        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setClaims(claims)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS512, SIGN)
                .compact();
    }

    /**
     * Проверка токена на валидность
     *
     * @param token токен
     * @return  boolean, валидный ли токен
     */
    public boolean validateToken(final String token){
        try{
            Jwts.parser().setSigningKey(SIGN).parseClaimsJws(token);
            return true;
        } catch (RuntimeException e){
                log.error(e.getMessage());
        }
        return false;
    }

    /**
     * Извлечение информации из токена
     *
     * @param token  токен
     * @return  Информацию, лежащую в токене
     */
    public Claims getClaims(String token){
        token = token.replace("\"", "");
        token = token.trim();

        try {
            return Jwts.parser()
                    .setSigningKey(SIGN)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (RuntimeException e){
            log.error("Неверный токен");
            log.error(e.getMessage() + "->" + e);
        }

        return null;
    }
}
