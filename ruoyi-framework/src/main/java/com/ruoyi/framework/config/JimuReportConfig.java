package com.ruoyi.framework.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

/**
 * Jimu报表（积木报表）配置类
 * 
 * 注意：Jimu报表的Spring Boot Starter会自动配置大部分内容
 * 系统的主数据源已经在 DruidConfig 中配置（使用 @Primary 注解），
 * Jimu报表会自动使用该数据源
 * 
 * 如果遇到 NullPointerException，可能是因为积木报表无法正确获取数据源。
 * 解决方案：
 * 1. 确保数据库表已创建（执行 jimureport.sql）
 * 2. 确保数据源配置正确
 * 3. 重启应用
 * 
 * @author ruoyi
 */
@Configuration
public class JimuReportConfig
{
    // 积木报表会自动使用 @Primary 标识的数据源（dynamicDataSource）
    // 如果仍有问题，可能需要检查：
    // 1. 数据库表是否正确创建
    // 2. 数据源连接是否正常
    // 3. 积木报表的配置是否正确
}

