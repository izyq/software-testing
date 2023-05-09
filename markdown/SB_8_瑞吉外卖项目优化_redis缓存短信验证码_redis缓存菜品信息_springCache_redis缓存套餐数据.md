---
created: 2022-02-18T03:18:17 (UTC +08:00)
tags: [SB_8_瑞吉外卖项目优化_redis缓存短信验证码_redis缓存菜品信息_springCache_redis缓存套餐数据]
source: https://blog.csdn.net/xujunming668/article/details/122378501
author: 
---

# (1条消息) SB_8_瑞吉外卖项目优化_redis缓存短信验证码_redis缓存菜品信息_springCache_redis缓存套餐数据_xujunming668的博客-CSDN博客

> ## Excerpt
> 瑞吉外卖项目优化-Day01课程内容环境搭建缓存短信验证码缓存菜品信息SpringCache缓存套餐数据前言1). 当前系统存在的问题之前我们已经实现了移动端菜品展示、点餐、购物车、下单等功能，但是由于移动端是面向所有的消费者的，请求压力相对比较大，而我们当前所有的数据查询都是从数据库MySQL中直接查询的，那么可能就存在如下问题： 频繁访问数据库，数据库访问压力大，系统性能下降，用户体验较差。2). 解决该问题的方法要解决我们上述提到的问题，就可以使用我们前面学习的一

---
## 前言

> 1). 当前系统存在的问题

之前我们已经实现了移动端菜品展示、点餐、购物车、下单等功能，但是由于移动端是面向所有的消费者的，请求压力相对比较大，而我们当前所有的数据查询都是从数据库MySQL中直接查询的，那么可能就存在如下问题： 频繁访问数据库，数据库访问压力大，系统性能下降，用户体验较差。

![image-20210819232120838](https://img-blog.csdnimg.cn/img_convert/300539ea00df6a8cc94ed520872e7181.png)

![1637973591438](https://img-blog.csdnimg.cn/img_convert/31a1915a09a305984d9d8b318306fbff.png)

> 2). 解决该问题的方法

要解决我们上述提到的问题，就可以使用我们前面学习的一个技术：Redis，通过Redis来做缓存，从而降低数据库的访问压力，提高系统的访问性能，从而提升用户体验。加入Redis做缓存之后，我们在进行数据查询时，就需要先查询缓存，如果缓存中有数据，直接返回，如果缓存中没有数据，则需要查询数据库，再将数据库查询的结果，缓存在redis中。

## 1\. 环境搭建

### 1.1 版本控制

接下来，我们就需要对我们的功能进行优化，但是需要说明的是，我们不仅仅要对上述提到的缓存进行优化，还需要对我们程序的各个方面进行优化。我们本章节主要是针对于缓存进行优化，为了方便的对我们各个优化版本的代码进行管理，我们使用Git来控制代码版本。 那么此时我们就需要将我们之前开发完成的代码提交到Git，并且推送到码云Gitee的远程仓库，执行步骤如下：

**1). 创建Gitee远程仓库**

![image-20210820000329886](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121015.png)

**2). idea-创建本地仓库**

![image-20210820000700459](https://img-blog.csdnimg.cn/img_convert/1318d95943a10491d2529f27157e4085.png)

**3). 准备忽略文件.gitignore**

在我们的项目中, 有一些文件是无需提交的到git，比如: .idea，target/，\*.iml等。我们可以直接将今天课程资料中提供的.gitignore 文件导入到我们的项目中。

![image-20210820001119649](https://img-blog.csdnimg.cn/img_convert/8cf542929d9e7852faae838a292c3f60.png)

**4). idea-提交并推送本地代码**

A. 添加项目文件进暂存区

![image-20210820001232154](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121023.png)

B. 提交代码

![image-20210820001805504](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121025.png)

![image-20210820002006653](https://img-blog.csdnimg.cn/img_convert/2f615bed124813076395847a1ffe60d9.png)

C. 推送代码到远程仓库

![image-20210820002159587](https://img-blog.csdnimg.cn/img_convert/ed7b161d512a3773e88f61196a45e2fb.png)

**5). 查看gitee远程仓库**

![image-20210820002723619](https://img-blog.csdnimg.cn/img_convert/d3d99e4344eb8f827268c21d4ac6b8ab.png)

**6). 创建分支**

目前默认git中只有一个主分支master，我们接下来进行缓存的优化，就不在master分支来操作了，我们需要在git上创建一个单独的分支v1.0，缓存的优化，我们就在该分支上进行操作。

![image-20210820003303544](https://img-blog.csdnimg.cn/img_convert/89138bf15afe5578838f389145ac646a.png)

当前创建的v1.0分支，是基于master分支创建出来的，所以目前master分支的代码， 和v1.0分支的代码是完全一样的，接下来把v1.0的代码也推送至远程仓库。

**7). 推送分支代码到远程**

![image-20210820003516900](https://img-blog.csdnimg.cn/img_convert/969ec80a7ce612c036c149df5c2407bb.png)

![image-20210820003545764](https://img-blog.csdnimg.cn/img_convert/4a55b44968b3f27d0e1c087f3d7afbf3.png) 系咯欧5

### 1.2 环境准备

**1). 在项目的pom.xml文件中导入spring data redis的maven坐标**

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

**2). 在项目的application.yml中加入redis相关配置**

```
  redis:
    host: 192.168.206.128
    port: 6379
    database: 0
    jedis:
      pool:
        max-active: 10
        max-idle: 5
```

注意: 引入上述依赖时,需要注意yml文件前面的缩进,上述配置应该配置在spring层级下面。

**3). 编写Redis的配置类RedisConfig,定义RedisTemplate**

```
package com.itheima.reggie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class MyRedisAutoConfig {


    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer()); 

        return template;
    }
}

```

**解释说明:**

1). 在SpringBoot工程启动时, 会加载一个自动配置类 RedisAutoConfiguration, 在里面已经声明了RedisTemplate这个bean  
![在这里插入图片描述](https://img-blog.csdnimg.cn/5762da82115c4e7ba04ae07c8595daa6.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAeHVqdW5taW5nNjY4,size_20,color_FFFFFF,t_70,g_se,x_16)

上述框架默认声明的RedisTemplate用的key和value的序列化方式是默认的 JdkSerializationRedisSerializer，如果key采用这种方式序列化，最终我们在测试时通过redis的图形化界面查询不是很方便，如下形式：

![image-20210822003112692](https://img-blog.csdnimg.cn/img_convert/60621e9a013270e6acb3fd2224ff972c.png)

2). 如果使用我们自定义的RedisTemplate, key的序列化方式使用的是StringRedisSerializer, 也就是字符串形式, 最终效果如下:

![image-20210822003408377](https://img-blog.csdnimg.cn/img_convert/784831179bbafe5f10660f63e129d418.png)

3). 定义了两个bean会不会出现冲突呢? 答案是不会, 因为源码如下:

![image-20210821092401172](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121046.png)

## 2\. 缓存短信验证码

### 2.1 思路分析

前面我们已经实现了移动端手机验证码登录，随机生成的验证码我们是保存在HttpSession中的。但是在我们实际的业务场景中，一般验证码都是需要设置过期时间的，如果存在HttpSession中就无法设置过期时间，此时我们就需要对这一块的功能进行优化。

现在需要改造为将验证码缓存在Redis中，具体的实现思路如下：

1). 在服务端UserController中注入RedisTemplate对象，用于操作Redis;

2). 在服务端UserController的sendMsg方法中，将随机生成的验证码缓存到Redis中，并设置有效期为5分钟;

3). 在服务端UserController的login方法中，从Redis中获取缓存的验证码，如果登录成功则删除Redis中的验证码;

### 2.2 代码改造

1). 在UserController中注入RedisTemplate对象，用于操作Redis

```
@Autowired
private RedisTemplate redisTemplate;
```

2). 在UserController的sendMsg方法中，将生成的验证码保存到Redis

![1637975596243](https://img-blog.csdnimg.cn/img_convert/28cda27dbb4143cf8bd527a8c9fb98ce.png)

```
 
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        
        String code =  "1234";

        
        
        
        
        
        redisTemplate.opsForValue().set(user.getPhone(),code,1, TimeUnit.MINUTES);  


        log.info("本次验证码："+ code);

        return R.success("发送成功");
    }

```

3.  登陆校验代码需要从redis中取出数据

![1637975646116](https://img-blog.csdnimg.cn/img_convert/5a15c0389a1ca763d7621dbb03841ede.png)

```

    
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String,String> param, HttpSession session){
        
        String phone = param.get("phone");  
        String inputCode = param.get("code"); 
        

        String verifyCode = (String) redisTemplate.opsForValue().get(phone);


        
        User user = userService.login(phone,inputCode,verifyCode);
        if (user!=null){
            
            redisTemplate.delete(phone);
            
            session.setAttribute("user",user.getId()); 
        }else{
            return R.error("登录失败");
        }
        return R.success(user);
    }

```

### 2.3 功能测试

代码编写完毕之后,重启服务。

**1). 访问前端工程，获取验证码**

![image-20210821200212767](https://img-blog.csdnimg.cn/img_convert/8bd75ee08057ecd07441fbe8c6805842.png)

通过控制台的日志，我们可以看到生成的验证码：

![image-20210822002439892](https://img-blog.csdnimg.cn/img_convert/8bd75ee08057ecd07441fbe8c6805842.png)

**2). 通过Redis的图形化界面工具查看Redis中的数据**

![image-20210822003550941](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121058.png)

**3). 在登录界面填写验证码登录完成后,查看Redis中的数据是否删除**

![image-20210822003732542](https://img-blog.csdnimg.cn/img_convert/548ed8c50d690cb8ad5c6ea9ed2a4ec7.png)

## 3\. 缓存菜品信息

### 3.1 实现思路

前面我们已经实现了移动端菜品查看功能，对应的服务端方法为DishController的list方法，此方法会根据前端提交的查询条件(categoryId)进行数据库查询操作。在高并发的情况下，频繁查询数据库会导致系统性能下降，服务端响应时间增长。现在需要对此方法进行缓存优化，提高系统的性能。

那么,我们又需要思考一个问题, 具体缓存几份数据呢, 所有的菜品缓存一份 , 还是说需要缓存多份呢? 我们可以看一下我们之前做的移动端效果:

![image-20210822010136819](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121102.png)

我们点击哪一个分类,展示的就是该分类下的菜品, 其他菜品无需展示。所以，这里面我们在缓存时，可以根据菜品的分类，缓存多份数据，页面在查询时，点击的是哪个分类，我们就查询该分类下的菜品缓存数据。

**具体的实现思路如下：**

1). 改造DishServiceImpl的list方法，先从Redis中获取分类对应的菜品数据，如果有则直接返回，无需查询数据库;如果没有则查询数据库，并将查询到的菜品数据存入Redis。

2). 改造DishController的save和update方法，加入清理缓存的逻辑。

> 注意：
> 
>  在使用缓存过程中，要注意保证数据库中的数据和缓存中的数据一致，如果数据库中的数据发生变化，需要及时清理缓存数据。否则就会造成缓存数据与数据库数据不一致的情况。

### 3.2 代码改造

需要改造的代码为： DishServiceImpl

#### 3.2.1 查询菜品缓存

改造的方法

redis的数据类型

redis缓存的key

redis缓存的value

list

string

dish\_分类Id\_状态 , 比如: dish\_12323232323\_1

List

**1). 在DishServiceImpl中注入RedisTemplate**

```
@Autowired
private RedisTemplate redisTemplate;
```

**2). 在list方法中,查询数据库之前,先查询缓存, 缓存中有数据, 直接返回**

```
  
    @Override
    public R<List<DishDto>> findByCategoryId(Long categoryId,Integer status) {
        
        String key = "dish_"+categoryId+"_"+status;
        List<DishDto> dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);


        
        if(dishDtoList==null) {
            
            List<Dish> dishList = dishMapper.findByCategoryId(categoryId, status);
            
               dishDtoList = dishList.stream().map(dish -> {
                DishDto dishDto = new DishDto();
                
                BeanUtils.copyProperties(dish, dishDto);
                
                List<DishFlavor> dishFlavorList = dishFlavorMapper.findByDishId(dish.getId());
                dishDto.setFlavors(dishFlavorList);
                return dishDto;
            }).collect(Collectors.toList());
            
            redisTemplate.opsForValue().set(key,dishDtoList);
        }


        return R.success(dishDtoList);
    }
```

![1637977624568](https://img-blog.csdnimg.cn/img_convert/21d6c0beb1bff32f7da1ffcf64ebb8de.png)

![1637977641310](https://img-blog.csdnimg.cn/img_convert/21d6c0beb1bff32f7da1ffcf64ebb8de.png)

#### 3.2.2 清理菜品缓存

为了保证数据库中的数据和缓存中的数据一致，如果数据库中的数据发生变化，需要及时清理缓存数据。所以，我们需要在添加菜品、更新菜品时清空缓存数据。

**1). 保存菜品,清空缓存**

在保存菜品的方法save中，当菜品数据保存完毕之后，需要清空菜品的缓存。那么这里清理菜品缓存的方式存在两种：

A. 清理所有分类下的菜品缓存

```

Set keys = redisTemplhate.keys("dish_*"); 
redisTemplate.delete(keys); 
```

B. 清理当前添加菜品分类下的缓存

```

String key = "dish_" + dishDto.getCategoryId();
redisTemplate.delete(key);
```

注意: 在这里我们推荐使用第一种方式进行清理，这样逻辑更加严谨。 因为对于修改操作，用户是可以修改菜品的分类的，如果用户修改了菜品的分类，那么原来分类下将少一个菜品，新的分类下将多一个菜品，这样的话，两个分类下的菜品列表数据都发生了变化。

**DishServiceImpl 保存新增菜品**

```
 
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        
        dishDto.setCreateTime(LocalDateTime.now());
        dishDto.setUpdateTime(LocalDateTime.now());
        dishDto.setSort(0);
        dishDto.setStatus(1);
        
        dishMapper.save(dishDto);  

        
        
        List<DishFlavor> flavors = dishDto.getFlavors();
        
      
      
        List<DishFlavor> dishFlavorList = flavors.stream().map((flavor -> {
            
            flavor.setDishId(dishDto.getId());
            flavor.setCreateTime(LocalDateTime.now());
            flavor.setUpdateTime(LocalDateTime.now());
            flavor.setCreateUser(dishDto.getCreateUser());
            flavor.setUpdateUser(dishDto.getUpdateUser());
            return flavor;
        })).collect(Collectors.toList());

        
        dishFlavorMapper.saveBatch(dishFlavorList);

        
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
    }
```

![1637818297484](https://img-blog.csdnimg.cn/img_convert/5b14ba32a5cdc51864d15c06420293d2.png)

DishServiceImpl 保存更新菜品

```
  
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        
        dishDto.setUpdateTime(LocalDateTime.now());
        
        dishMapper.updateById(dishDto);

        
        dishFlavorMapper.deleteByDishId(dishDto.getId());

        
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> dishFlavorList = flavors.stream().map((flavor -> {
            
            flavor.setDishId(dishDto.getId());
            flavor.setCreateTime(LocalDateTime.now());
            flavor.setUpdateTime(LocalDateTime.now());
            flavor.setCreateUser(dishDto.getCreateUser());
            flavor.setUpdateUser(dishDto.getUpdateUser());
            return flavor;
        })).collect(Collectors.toList());

        
        dishFlavorMapper.saveBatch(dishFlavorList);

        
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

    }
```

![1637818350109](https://img-blog.csdnimg.cn/img_convert/8e2e550cf110223b9a5880cc4581551a.png)

### 3.3 功能测试

代码编写完毕之后,重新启动服务。

1). 访问移动端，根据分类查询菜品列表，然后再检查Redis的缓存数据，是否可以正常缓存；

![image-20210822221038509](https://img-blog.csdnimg.cn/img_convert/bf5904d0f49d1f036a2dde26458d7223.png)

我们也可以在服务端，通过debug断点的形式一步一步的跟踪代码的执行。

2). 当我们在进行新增及修改菜品时, 查询Redis中的缓存数据, 是否被清除;

### 3.4 提交并推送代码

**1). 提交并推送代码**

在v1.0分支中, 将我们已经实现并且测试通过的使用redis缓存验证码和菜品信息的代码,提交并推送至Gitee

![image-20210822222206452](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121121.png) ![image-20210822222244727](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121123.png)

**2). 合并代码到master分支**

A. 将代码切换到master分支

![image-20210822222756572](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121126.png)

B. 将v1.0分支的代码合并到当前master分支

![image-20210822223314087](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121128.png)

C. 将master分支合并后代码推送到Gitee

![image-20210822223837020](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121129.png) ![image-20210822223912803](https://gitee.com/yydsjava/img123/raw/master/javayyds/img123/20220108121132.png)

## 4\. Spring Cache

### 4.1 介绍

**Spring Cache**是一个框架，实现了基于注解的缓存功能，只需要简单地加一个注解，就能实现缓存功能，大大简化我们在业务中操作缓存的代码。

Spring Cache只是提供了一层抽象，底层可以切换不同的cache实现。具体就是通过**CacheManager**接口来统一不同的缓存技术。CacheManager是Spring提供的各种缓存技术抽象接口。

针对不同的缓存技术需要实现不同的CacheManager：

**CacheManager**

**描述**

EhCacheCacheManager

使用EhCache作为缓存技术

GuavaCacheManager

使用Google的GuavaCache作为缓存技术

RedisCacheManager

使用Redis作为缓存技术

spring 自己也搞了一套缓存技术，默认的缓存  
spring缓存是缓存在Map集合中

### 4.2 注解

在SpringCache中提供了很多缓存操作的注解，常见的是以下的几个：

**注解**

**说明**

@EnableCaching

开启缓存注解功能

@Cacheable

在方法执行前spring先查看缓存中是否有数据，如果有数据，则直接返回缓存数据；若没有数据，调用方法并将方法返回值放到缓存中

@CachePut

将方法的返回值或者参数放到缓存中

@CacheEvict

将一条或多条数据从缓存中删除

在spring boot项目中，使用缓存技术只需在项目中导入相关缓存技术的依赖包，并在启动类上使用@EnableCaching开启缓存支持即可。

例如，使用Redis作为缓存技术，只需要导入Spring data Redis的maven坐标即可。

### 4.3 入门程序

接下来，我们将通过一个入门案例来演示一下SpringCache的常见用法。 上面我们提到，SpringCache可以集成不同的缓存技术，如Redis、Ehcache甚至我们可以使用Map来缓存数据， 接下来我们在演示的时候，就先通过一个Map来缓存数据，最后我们再换成Redis来缓存。

#### 4.3.1 环境准备

**1). 数据库准备**

将今天资料中的SQL脚本直接导入数据库中。  
![在这里插入图片描述](https://img-blog.csdnimg.cn/a619673ac3bd4f2ab422dc3eccca22c9.png)

**2). 导入基础工程**

基础环境的代码，在我们今天的资料中已经准备好了， 大家只需要将这个工程导入进来就可以了。导入进来的工程结构如下：

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.4.5</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.itheima</groupId>
    <artifactId>cache_demo_copy</artifactId>
    <version>1.0-SNAPSHOT</version>
    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.76</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.20</version>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.76</version>
        </dependency>

        <dependency>
            <groupId>commons-lang</groupId>
            <artifactId>commons-lang</artifactId>
            <version>2.6</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.27</version>
            <scope>runtime</scope>
        </dependency>


        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.0</version>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.23</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.4.5</version>
            </plugin>
        </plugins>
    </build>
</project>
```

![image-20210822225934512](https://img-blog.csdnimg.cn/img_convert/d0a8976fa7ce6a79675128ec63feb02c.png)

由于SpringCache的基本功能是Spring核心(spring-context)中提供的，所以目前我们进行简单的SpringCache测试，是可以不用额外引入其他依赖的。

**3). 注入CacheManager**

我们可以在UserController注入一个CacheManager，在Debug时，我们可以通过CacheManager跟踪缓存中数据的变化。

![在这里插入图片描述](https://img-blog.csdnimg.cn/f786df6848274beaad497cf3b91685cb.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAeHVqdW5taW5nNjY4,size_20,color_FFFFFF,t_70,g_se,x_16)

我们可以看到CacheManager是一个接口，默认的实现有以下几种 ；

![image-20210822231217450](https://img-blog.csdnimg.cn/img_convert/5ca26d549c1949a69eb5f10900efb913.png)

而在上述的这几个实现中，默认使用的是 ConcurrentMapCacheManager。稍后我们可以通过断点的形式跟踪缓存数据的变化。

**4). 引导类上加@EnableCaching**

在引导类上加该注解，就代表当前项目开启缓存注解功能。

![image-20210822231616569](https://img-blog.csdnimg.cn/img_convert/5ca26d549c1949a69eb5f10900efb913.png)

#### 4.3.2 @CachePut注解

> @CachePut 说明：
> 
>  作用: 将方法返回值，放入缓存
> 
>  value: 缓存的名称, 每个缓存名称下面可以有很多key
> 
>  key: 缓存的key ----------> 支持Spring的表达式语言SPEL语法

**1). 在save方法上加注解@CachePut**

当前UserController的save方法是用来保存用户信息的，我们希望在该用户信息保存到数据库的同时，也往缓存中缓存一份数据，我们可以在save方法上加上注解 @CachePut，用法如下：

```

@CachePut(value = "userCache", key = "#user.id")
@PostMapping
public User save(User user){
    userService.save(user);
    return user;
}
```

> key的写法如下：
> 
>  #user.id : #user指的是方法形参的名称, id指的是user的id属性 , 也就是使用user的id属性作为key ;
> 
>  #user.name: #user指的是方法形参的名称, name指的是user的name属性 ,也就是使用user的name属性作为key ;
> 
> 
> 
>  #result.id : #result代表方法返回值，该表达式 代表以返回对象的id属性作为key ；
> 
>  #result.name : #result代表方法返回值，该表达式 代表以返回对象的name属性作为key ；

**2). 测试**

启动服务,通过postman请求访问UserController的方法, 然后通过断点的形式跟踪缓存数据。

![image-20210822233438182](https://img-blog.csdnimg.cn/img_convert/0caf661719d108455aa1f8419b85c742.png)

第一次访问时，缓存中的数据是空的，因为save方法执行完毕后才会缓存数据。

![image-20210822233724439](https://img-blog.csdnimg.cn/img_convert/3a539bfbecf5b78ff92466c53f8e6b8a.png)

第二次访问时，我们通过debug可以看到已经有一条数据了，就是上次保存的数据，已经缓存了，缓存的key就是用户的id。

![image-20210822234105085](https://img-blog.csdnimg.cn/img_convert/4a20fb977d8ab68afa3082763e24eb9b.png)

注意: 上述的演示，最终的数据，实际上是缓存在ConcurrentHashMap中，那么当我们的服务器重启之后，缓存中的数据就会丢失。 我们后面使用了Redis来缓存就不存在这样的问题了。

#### 4.3.3 @CacheEvict注解

> @CacheEvict 说明：
> 
>  作用: 清理指定缓存
> 
>  value: 缓存的名称，每个缓存名称下面可以有多个key
> 
>  key: 缓存的key ----------> 支持Spring的表达式语言SPEL语法

**1). 在 delete 方法上加注解@CacheEvict**

当我们在删除数据库user表的数据的时候,我们需要删除缓存中对应的数据,此时就可以使用@CacheEvict注解, 具体的使用方式如下:

```

@CacheEvict(value = "userCache",key = "#p0")  


@DeleteMapping("/{id}")
public void delete(@PathVariable Long id){
    userService.removeById(id);
}
```

**2). 测试**

要测试缓存的删除，我们先访问save方法4次，保存4条数据到数据库的同时，也保存到缓存中，最终我们可以通过debug看到缓存中的数据信息。 然后我们在通过postman访问delete方法， 如下：

![image-20210823000431356](https://img-blog.csdnimg.cn/img_convert/ecd5986ca454d3c3572a48b8d7daad96.png)

删除数据时，通过debug我们可以看到已经缓存的4条数据：

![image-20210823000458089](https://img-blog.csdnimg.cn/img_convert/a07fd4c89a8cc74b9d72adc38b272166.png)

当执行完delete操作之后，我们再次保存一条数据，在保存的时候debug查看一下删除的ID值是否已经被删除。

![image-20210823000733218](https://img-blog.csdnimg.cn/img_convert/b93919b7e43b97af814ed2d4e5883bcf.png)

**3). 在 update 方法上加注解@CacheEvict**

在更新数据之后，数据库的数据已经发生了变更，我们需要将缓存中对应的数据删除掉，避免出现数据库数据与缓存数据不一致的情况。

```



@CacheEvict(value = "userCache",key = "#result.id")         
@PutMapping
public User update(User user){
    userService.updateById(user);
    return user;
}
```

加上注解之后，我们可以重启服务，然后测试方式，基本和上述相同，先缓存数据，然后再更新某一条数据，通过debug的形式查询缓存数据的情况。

#### 4.3.4 @Cacheable注解

> @Cacheable 说明:
> 
>  作用: 在方法执行前，spring先查看缓存中是否有数据，如果有数据，则直接返回缓存数据；若没有数据，调用方法并将方法返回值放到缓存中
> 
>  value: 缓存的名称，每个缓存名称下面可以有多个key
> 
>  key: 缓存的key ----------> 支持Spring的表达式语言SPEL语法

**1). 在getById上加注解@Cacheable**

```

@Cacheable(value = "userCache",key = "#id")
@GetMapping("/{id}")
public User getById(@PathVariable Long id){
    User user = userService.getById(id);
    return user;
}
```

**2). 测试**

我们可以重启服务，然后通过debug断点跟踪程序执行。我们发现，第一次访问，会请求我们controller的方法，查询数据库。后面再查询相同的id，就直接获取到数据库，不用再查询数据库了，就说明缓存生效了。

![image-20210823002517941](https://img-blog.csdnimg.cn/img_convert/865a8b08076337b04685170310680461.png)

当我们在测试时，查询一个数据库不存在的id值，第一次查询缓存中没有，也会查询数据库。而第二次再查询时，会发现，不再查询数据库了，而是直接返回，那也就是说如果根据ID没有查询到数据,那么会自动缓存一个null值。 我们可以通过debug，验证一下：

![image-20210823002907048](https://img-blog.csdnimg.cn/img_convert/64412b622fed21f127c6799f7fa0ebaf.png)

我们能不能做到，当查询到的值不为null时，再进行缓存，如果为null，则不缓存呢? 答案是可以的。

**3). 缓存非null值**

在@Cacheable注解中，提供了两个属性分别为： condition， unless 。

> condition : 表示满足什么条件, 再进行缓存 ;
> 
> unless : 表示满足条件则不缓存 ; 与上述的condition是反向的 ;

具体实现方式如下:

```
    @GetMapping("/{id}")
    
    @Cacheable(value = "users",key="#id",unless = "#result==null")
    public User getById(@PathVariable Long id){
        User user = userService.findById(id);
        return user;
    }

```

注意： 此处，我们使用的时候只能够使用 unless， 因为在condition中，我们是无法获取到结果 #result的。

### 4.4 集成Redis

在使用上述默认的ConcurrentHashMap做缓存时，服务重启之后，之前缓存的数据就全部丢失了，操作起来并不友好。在项目中使用，我们会选择使用redis来做缓存，主要需要操作以下几步：

1). pom.xml

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

2). application.yml

```
spring:
  redis:
    host: 192.168.200.200
    port: 6379
    password: root@123456
    database: 0
  cache:
    redis:
      time-to-live: 1800000   
```

3). 测试

重新启动项目，通过postman发送根据id查询数据的请求，然后通过redis的图形化界面工具，查看redis中是否可以正常的缓存数据。

![image-20210823010810680](https://img-blog.csdnimg.cn/img_convert/b2e4e57ac04af6efe2365aaf5653af6e.png)

![image-20210823010742530](https://img-blog.csdnimg.cn/img_convert/685e06745437023b57f36cb5fe05bdd6.png)

## 5\. 缓存套餐数据

### 5.1 实现思路

前面我们已经实现了移动端套餐查看功能，对应的服务端方法为SetmealController的list方法，此方法会根据前端提交的查询条件进行数据库查询操作。在高并发的情况下，频繁查询数据库会导致系统性能下降，服务端响应时间增长。现在需要对此方法进行缓存优化，提高系统的性能。

具体的实现思路如下：

1). 导入Spring Cache和Redis相关maven坐标

2). 在application.yml中配置缓存数据的过期时间

3). 在启动类上加入@EnableCaching注解，开启缓存注解功能

4). 在SetmealController的list方法上加入@Cacheable注解

5). 在SetmealController的save和delete方法上加入CacheEvict注解

### 5.2 缓存套餐数据

#### 5.2.1 代码实现

1). pom.xml中引入依赖

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

备注: spring-boot-starter-data-redis 这个依赖前面已经引入了, 无需再次引入。

2). application.yml中设置缓存过期时间

```
spring:  
  cache:
    redis:
      time-to-live: 1800000 
```

3). 启动类上加入@EnableCaching注解

![在这里插入图片描述](https://img-blog.csdnimg.cn/855dd2d29bd147a7813e2bc613c564b2.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAeHVqdW5taW5nNjY4,size_20,color_FFFFFF,t_70,g_se,x_16)

4). SetmealServiceImpl的list方法上加入@Cacheable注解

在进行套餐数据查询时，我们需要根据分类ID和套餐的状态进行查询，所以我们在缓存数据时，可以将套餐分类ID和套餐状态组合起来作为key，如： 1627182182\_1 (1627182182为分类ID，1为状态)。

```

    
    @Override
    @Cacheable(value = "setmealCache",key = "#categoryId+'_'+#status")
    public List<Setmeal> list(Long categoryId,Integer status) {
        log.info("=============查询数据库得到套餐数据=======");
        List<Setmeal> setmealList = setMealMapper.list(categoryId,status);
        return setmealList;
    }
```

### 5.3 清理套餐数据

#### 5.3.1 代码实现

为了保证数据库中数据与缓存数据的一致性，在我们添加套餐或者删除套餐数据之后，需要清空当前套餐缓存的全部数据。那么@CacheEvict注解如何清除某一份缓存下所有的数据呢，这里我们可以指定@CacheEvict中的一个属性 allEnties，将其设置为true即可。

```
allEntries属性解释
       allEntries是boolean类型，表示是否需要清除缓存中的所有元素。默认为false，表示不需要。当指定了allEntries为true时，Spring Cache将忽略指定的key。有的时候我们需要Cache一下清除所有的元素，这比一个一个清除元素更有效率。


   @CacheEvict(value="users", allEntries=true)
   public void delete(Integer id) {
      System.out.println("delete user by id: " + id);

   }
```

**1). 在saveWithDish方法上加注解@CacheEvict**

```


    @Override
    @CacheEvict(value = "setmealCache",allEntries = true)
    public void saveWithDish(SetmealDto setmealDto) {

        setmealDto.setUpdateTime(LocalDateTime.now());
        setmealDto.setCreateTime(LocalDateTime.now());
        setmealDto.setStatus(0);
        setMealMapper.saveWithDish(setmealDto);


        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((setmealDish) -> {
            setmealDish.setSetmealId(setmealDto.getId());
            setmealDish.setCreateTime(LocalDateTime.now());
            setmealDish.setUpdateTime(LocalDateTime.now());
            setmealDish.setSort(0);
            setmealDish.setCreateUser(setmealDto.getCreateUser());
            setmealDish.setUpdateUser(setmealDto.getUpdateUser());
            return setmealDish;
        }).collect(Collectors.toList());


        
        setmealDishMapper.saveBatch(setmealDishes);
    }
```

**2). 在delete方法上加注解@CacheEvict**

```
 @Override
    @CacheEvict(value = "setmealCache",allEntries = true)
    public void removeWithDish(List<Long> ids) {

        long count = setMealMapper.queryDishWithStatus(ids);
        if(count>0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        setMealMapper.removeByIds(ids);


        setmealDishMapper.removeByIds(ids);
    }
```

#### 5.3.2 测试

代码编写完成之后,重启工程,然后访问后台管理系统,对套餐数据进行新增 以及 删除, 然后通过Redis的图形化界面工具,查看Redis中的套餐缓存是否已经被删除。

### 5.4 提交推送代码

到目前为止，我们已经在v1.0这个分支中完成了套餐数据的缓存，接下来我们就需要将代码提交并推送到远程仓库。

![image-20210823235612400](https://img-blog.csdnimg.cn/img_convert/48b08f218fe8fc47251f313eab5a30fe.png)

然后，在idea中切换到master分支，然后将v1.0分支的代码合并到master。

![image-20210823235822139](https://img-blog.csdnimg.cn/img_convert/c6074d626c538a735278ab0381f34929.png)

再将合并后的master分支的代码，推送到远程仓库。

![image-20210824000057260](https://img-blog.csdnimg.cn/img_convert/e02f4b312c4c7b3f08651d0dde3582f5.png)

mealDto.getUpdateUser());  
return setmealDish;  
}).collect(Collectors.toList());

// 3 批量保存套餐关联的菜品集合  
//保存套餐和菜品的关联信息，操作setmeal\_dish,执行insert操作  
setmealDishMapper.saveBatch(setmealDishes);  
}

````



**2). 在delete方法上加注解@CacheEvict**

```java
 @Override
    @CacheEvict(value = "setmealCache",allEntries = true)
    public void removeWithDish(List<Long> ids) {
//        A. 查询该批次套餐中是否存在售卖中的套餐, 如果存在, 不允许删除
        long count = setMealMapper.queryDishWithStatus(ids);
        if(count>0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
//       2. 删除套餐数据
        setMealMapper.removeByIds(ids);

//      3. 删除套餐关联的菜品数据
        setmealDishMapper.removeByIds(ids);
    }
````

#### 5.3.2 测试

代码编写完成之后,重启工程,然后访问后台管理系统,对套餐数据进行新增 以及 删除, 然后通过Redis的图形化界面工具,查看Redis中的套餐缓存是否已经被删除。

### 5.4 提交推送代码

到目前为止，我们已经在v1.0这个分支中完成了套餐数据的缓存，接下来我们就需要将代码提交并推送到远程仓库。

![image-20210823235612400](https://img-blog.csdnimg.cn/img_convert/450406acb4352f2a2995308dabec9549.png)

然后，在idea中切换到master分支，然后将v1.0分支的代码合并到master。

![image-20210823235822139](https://img-blog.csdnimg.cn/img_convert/c519a419a009d55ad2a9a9750194cb98.png)

再将合并后的master分支的代码，推送到远程仓库。

![image-20210824000057260](https://img-blog.csdnimg.cn/img_convert/f42f6d9ebf9c099b54fc47f5e27142b5.png)
