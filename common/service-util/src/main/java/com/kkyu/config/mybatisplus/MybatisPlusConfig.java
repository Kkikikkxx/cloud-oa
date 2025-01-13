package com.kkyu.config.mybatisplus;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 该类用于配置 MyBatis-Plus 的核心插件及分页拦截器。
 * 1. 使用 @Configuration 标注为 Spring 的配置类。
 * 2. 使用 @MapperScan 扫描指定的 Mapper 接口路径。
 * 3. 配置了 MyBatis-Plus 分页拦截器，用于自动处理分页逻辑。
 *
 * @author kkyu
 * @since 2025-01-10
 */
@Slf4j
@Configuration
@MapperScan("com.kkyu.auth.mapper")  // 指定 MyBatis Mapper 接口的扫描路径
public class MybatisPlusConfig {

    /**
     * 配置 MyBatis-Plus 插件拦截器
     * 该方法配置了 MyBatis-Plus 的分页插件。
     * 使用 MybatisPlusInterceptor 插件，添加了 PaginationInnerInterceptor 分页拦截器。
     *
     * @return 返回 MyBatis-Plus 拦截器实例，带有分页功能
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建 MyBatis-Plus 拦截器实例
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加 MySQL 分页拦截器，用于支持分页查询
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
