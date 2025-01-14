package com.kkyu.common.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * JwtHelper 类用于生成、解析和验证基于 JWT（JSON Web Token）的令牌。
 * 使用了 jjwt 库来实现 JWT 相关的操作。
 */
@Slf4j
public class JwtHelper {

    /**
     * Token 有效期，设置为 1 天 (以毫秒计)。
     */
    private final static long tokenExpiration =  24 * 60 * 60 * 1000;

    /**
     * Token 签名密钥，用于生成和解析 JWT 的签名。
     */
    private final static String tokenSignKey = "kkikikk";

    /**
     * 创建 JWT 令牌的方法。
     *
     * @param userId 用户 ID，用于存储在令牌中的自定义声明。
     * @param username 用户名，用于存储在令牌中的自定义声明。
     * @return 生成的 JWT 令牌字符串。
     */
    public static String createToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject("AUTH-USER")  // 设置主题
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))  // 设置过期时间
                .claim("userId", userId)  // 添加自定义声明：用户ID
                .claim("username", username)  // 添加自定义声明：用户名
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)  // 使用 HS512 算法和密钥进行签名
                .compressWith(CompressionCodecs.GZIP)  // 使用 GZIP 压缩
                .compact();  // 生成并返回最终的 JWT 字符串
    }

    /**
     * 从令牌中解析并提取用户 ID。
     *
     * @param token JWT 令牌字符串
     * @return 用户 ID，如果解析失败或 token 为空，则返回 null。
     */
    public static Long getUserId(String token) {
        try {
            // 校验 token 是否为空
            if (StringUtils.isEmpty(token)) return null;

            // 解析令牌，使用密钥验证签名
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();

            // 获取 userId，并转换为 Long 类型
            Integer userId = (Integer) claims.get("userId");
            return userId.longValue();
        } catch (Exception e) {
            // 使用 log.error 记录异常信息
            log.error("解析用户ID时出现异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从令牌中解析并提取用户名。
     *
     * @param token JWT 令牌字符串
     * @return 用户名，如果解析失败或 token 为空，则返回空字符串。
     */
    public static String getUsername(String token) {
        try {
            if (StringUtils.isEmpty(token)) return "";

            // 解析令牌并提取用户名
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("username");
        } catch (Exception e) {
            // 使用 log.error 记录异常信息
            log.error("解析用户名时出现异常: {}", e.getMessage(), e);
            return null;
        }
    }
}
