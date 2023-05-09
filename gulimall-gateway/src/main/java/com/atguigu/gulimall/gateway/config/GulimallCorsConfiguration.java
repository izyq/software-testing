// package com.atguigu.gulimall.gateway.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
// import org.springframework.web.cors.reactive.CorsWebFilter;
//
// /**
//  * 跨域配置类
//  * @title: CorsConfiguration
//  * @Author cxcc
//  * @Date: 2021/12/21 下午 8:39
//  */
// @Configuration//配置类注解
//
// public class GulimallCorsConfiguration {
// 	//将该方法返回值作为bean注入spring容器
// 	@Bean
// 	public CorsWebFilter corsWebFilter(){
// 		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
// 		CorsConfiguration corsConfiguration=new CorsConfiguration();
// 		//1.配置跨域
// 		//1.1允许哪些头跨域(*代表全部)
// 		corsConfiguration.addAllowedHeader("*");
// 		//1.2允许哪些请求方式跨域(*代表全部)
// 		corsConfiguration.addAllowedMethod("*");
// 		//1.3允许哪个请求来源跨域(*代表全部)
// 		corsConfiguration.addAllowedOrigin("*");
// 		//1.4是否允许携带cookie跨域(true代表允许)
// 		corsConfiguration.setAllowCredentials(true);
//
// 		source.registerCorsConfiguration("/**",corsConfiguration);
// 		return new CorsWebFilter(source);
// 	}
//
// }
