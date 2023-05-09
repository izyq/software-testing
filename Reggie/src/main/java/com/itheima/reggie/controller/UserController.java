package com.itheima.reggie.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 客户登陆
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest req;

    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
     * 发送手机验证码
     * @param user
     * @return
     * @throws ClientException
     */
    @PostMapping("/sendMsg")
    public R sendMsg(@RequestBody User user) throws ClientException {
          if (user == null){
              return R.error("非法参数");
          }
        Random random = new Random();
        int code = random.nextInt(900000)+100000;
        System.out.println("验证码:"+code);
        //SendSms.sendMsg(user.getPhone(),code+"");

        /*HttpSession session = req.getSession();
        session.setAttribute(user.getPhone(),code+"");*/
        redisTemplate.opsForValue().set(user.getPhone(),code+"",5*60, TimeUnit.SECONDS);
        
        return R.success("发送验证码成功:"+code);
    }
    
    /**
     * 移动端用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody Map map){

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();

        HttpSession session = req.getSession();
        //String codeSession = (String) session.getAttribute(phone);
        String codeSession = (String) redisTemplate.opsForValue().get(phone);
        if(code == null){
            return R.error("验证码为空");
        }
        if (!code.equals(codeSession)){
            return R.error("验证码不正确");
        }

        redisTemplate.delete(phone);

        R r = userService.login(phone);
        if (r.getCode() == 1){
            session.setAttribute("user",((User)r.getData()).getId());
        }
        return r;

    }
    
    /**
     * 用户退出
     * @return
     */
    @PostMapping("/loginout")
    public R loginout(){
        HttpSession session = req.getSession();
        session.invalidate();

        return R.success("退出成功");
    }

}
