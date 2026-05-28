package com.liminghan.market.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Component
public class JwtUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire-seconds:86400}")
    private long expireSeconds;

    public String generateToken(Long userId, String username, String role) {
        long expireAt = Instant.now().getEpochSecond() + expireSeconds;
        String header = encodeJson(Map.of("alg", "HS256", "typ", "JWT"));
        String payload = encodeJson(Map.of(
                "userId", userId,
                "username", username,
                "role", role,
                "exp", expireAt
        ));
        String unsignedToken = header + "." + payload;
        return unsignedToken + "." + sign(unsignedToken);
    }

    public SecurityUser parseToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            String unsignedToken = parts[0] + "." + parts[1];
            if (!sign(unsignedToken).equals(parts[2])) {
                return null;
            }
            Map<String, Object> payload = OBJECT_MAPPER.readValue(decode(parts[1]), new TypeReference<>() {
            });
            long exp = ((Number) payload.get("exp")).longValue();
            if (exp < Instant.now().getEpochSecond()) {
                return null;
            }
            Long userId = ((Number) payload.get("userId")).longValue();
            String username = String.valueOf(payload.get("username"));
            String role = String.valueOf(payload.get("role"));
            return new SecurityUser(userId, username, role);
        } catch (Exception exception) {
            return null;
        }
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(OBJECT_MAPPER.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new IllegalStateException("failed to build jwt", exception);
        }
    }

    private byte[] decode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("failed to sign jwt", exception);
        }
    }
}
