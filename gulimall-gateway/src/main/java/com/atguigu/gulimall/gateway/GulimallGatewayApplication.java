package com.atguigu.gulimall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *  1.开启服务注册发现
 *  (配置nacos的注册中心地址)
 */
@EnableDiscoveryClient //开启alibaba Nacos注册中心客户端
//本模块不需要数据源依赖,设置这个,用于排除数据源的依赖,也可以在pom文件配置
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class GulimallGatewayApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(GulimallGatewayApplication.class, args);
	}
	
}
