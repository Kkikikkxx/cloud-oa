package com.kkyu.jwt;

import com.kkyu.common.jwt.JwtHelper;
import org.junit.jupiter.api.Test;

public class JwtTest {

    /**
     * 测试主方法，用于创建令牌并解析其中的用户信息。
     * 用于本地测试，生产环境下不建议直接调用。
     */
    @Test
    public void Token() {
        // 测试创建 JWT 令牌
        String token = JwtHelper.createToken(1L, "admin");
        System.out.println("生成的 Token: " + token);

        // 测试解析用户ID和用户名
        System.out.println("解析的用户ID: " + JwtHelper.getUserId(token));
        System.out.println("解析的用户名: " + JwtHelper.getUsername(token));
    }
}
