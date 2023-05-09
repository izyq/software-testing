---
created: 2022-02-18T03:09:56 (UTC +08:00)
tags: [瑞吉外卖基于springboot+vue实现小程序点餐系统]
source: https://blog.csdn.net/weixin_46408092/article/details/122746329
author: 
---

# (1条消息) 瑞吉外卖基于springboot+vue实现小程序点餐系统_熙媛的博客-CSDN博客

> ## Excerpt
> 功能概述技术栈1). 用户层本项目中在构建系统管理后台的前端页面，我们会用到H5、Vue.js、ElementUI等技术。而在构建移动端应用时，我们会使用到微信小程序。2). 网关层Nginx是一个服务器，主要用来作为Http服务器，部署静态资源，访问性能高。在Nginx中还有两个比较重要的作用： 反向代理和负载均衡， 在进行项目部署时，要实现Tomcat的负载均衡，就可以通过Nginx来实现。3). 应用层SpringBoot： 快速构建Spring项目, 采用 "约定优.

---
## 功能概述

![](https://img-blog.csdnimg.cn/6bb025e2e4774227803a5b2cf38c8ec1.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA54aZ5aqb,size_20,color_FFFFFF,t_70,g_se,x_16)

##  [技术栈](https://so.csdn.net/so/search?q=%E6%8A%80%E6%9C%AF%E6%A0%88&spm=1001.2101.3001.7020)![](https://img-blog.csdnimg.cn/cd4f1eb617eb4ec1b38c6a1ef2110ac0.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA54aZ5aqb,size_20,color_FFFFFF,t_70,g_se,x_16)

1). 用户层

本项目中在构建系统管理后台的前端页面，我们会用到H5、Vue.js、[ElementUI](https://so.csdn.net/so/search?q=ElementUI&spm=1001.2101.3001.7020)等技术。而在构建移动端应用时，我们会使用到微信小程序。

2). 网关层

[Nginx](https://so.csdn.net/so/search?q=Nginx&spm=1001.2101.3001.7020)是一个服务器，主要用来作为Http服务器，部署静态资源，访问性能高。在Nginx中还有两个比较重要的作用： 反向代理和负载均衡， 在进行项目部署时，要实现Tomcat的负载均衡，就可以通过Nginx来实现。

3). 应用层

**SpringBoot： 快速构建Spring项目, 采用 "约定优于配置" 的思想, 简化Spring项目的配置开发。**

Spring: 统一管理项目中的各种资源(bean), 在web开发的各层中都会用到。

SpringMVC：SpringMVC是spring框架的一个模块，springmvc和spring无需通过中间整合层进行整合，可以无缝集成。

![](https://img-blog.csdnimg.cn/0c501d5667a849df95097ee8ab255b90.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA54aZ5aqb,size_20,color_FFFFFF,t_70,g_se,x_16)

**SpringSession: 主要解决在集群环境下的Session共享问题。**

lombok：能以简单的注解形式来简化java代码，提高开发人员的开发效率。例如开发中经常需要写的javabean，都需要花时间去添加相应的getter/setter，也许还要去写构造器、equals等方法。

Swagger： 可以自动的帮助开发人员生成接口文档，并对接口进行测试。

4). 数据层

MySQL： 关系型数据库, 本项目的核心业务数据都会采用MySQL进行存储。

MybatisPlus： 本项目持久层将会使用MybatisPlus来简化开发, 基本的单表增删改查直接调用框架提供的方法即可。

Redis： 基于key-value格式存储的内存数据库, 访问速度快, 经常使用它做缓存(降低数据库访问压力, 提供访问效率), 在后面的性能优化中会使用。

1). 移动端前台功能

![](https://img-blog.csdnimg.cn/62d112fee4e3406dbce0016622cb040c.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA54aZ5aqb,size_20,color_FFFFFF,t_70,g_se,x_16)

手机号登录 , 微信登录 , 收件人地址管理 , 用户历史订单查询 , 菜品规格查询 , 购物车功能 , 下单 , 分类及菜品浏览。

2). 系统管理后台功能

员工登录/退出 , 员工信息管理 , 分类管理 , 菜品管理 , 套餐管理 , 菜品口味管理 , 订单管理 。

2.5 角色  
在瑞吉外卖这个项目中，存在以下三种用户，这三种用户对应三个角色： 后台系统管理员、后台系统普通员工、C端(移动端)用户。
